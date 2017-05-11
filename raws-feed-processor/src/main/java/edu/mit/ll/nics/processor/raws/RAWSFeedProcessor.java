package edu.mit.ll.nics.processor.raws;

import com.google.common.base.Stopwatch;
import edu.mit.ll.nics.processor.DataStoreManager;
import edu.mit.ll.nics.processor.factory.RAWSFeatureFactory;
import edu.mit.ll.nics.processor.raws.model.RAWSFeature;
import edu.mit.ll.nics.processor.raws.model.RAWSResponse;
import edu.mit.ll.nics.processor.raws.parser.RAWSResponseParser;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;
import org.geotools.data.*;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.text.cql2.CQL;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.FactoryException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RAWSFeedProcessor implements Processor {

    private static final Logger logger = Logger.getLogger(RAWSFeedProcessor.class.getSimpleName());
    private DataStoreManager dataStoreManager;
    private String rawsFeatureSource;
    private RAWSResponseParser rawsResponseParser;
    private RAWSFeatureFactory rawsFeatureFactory;

    public RAWSFeedProcessor(DataStoreManager dataStoreManager, String rawsFeatureSource, RAWSResponseParser rawsResponseParser,
                             RAWSFeatureFactory rawsFeatureFactory) {
        this.dataStoreManager = dataStoreManager;
        this.rawsFeatureSource= rawsFeatureSource;
        this.rawsResponseParser = rawsResponseParser;
        this.rawsFeatureFactory = rawsFeatureFactory;
    }

    public RAWSFeedProcessor() {
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String geoJsonString = exchange.getIn().getBody(String.class);
        logger.trace("RAWS API raw response string: " + geoJsonString);
        RAWSResponse response = rawsResponseParser.parse(geoJsonString);
        logger.trace("Parsed response: " + response);
        if(response.hasErrors()) {
            logger.error(String.format("Unable to process RAWS API Response, Error Message: %s, Error Code: %s", response.getRAWSSummary().getResponseMessage(), response.getRAWSSummary().getResponseCode()));
            return;
        }
        this.persistFeatures(response.getRAWSFeatures());
    }

    private void persistFeatures(List<RAWSFeature> rawsFeatures) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        if(rawsFeatures.size() == 0) {
            logger.info("Zero RAWS features to process, exiting current process");
            return;
        }
        logger.debug(String.format("Processing %d RAWS features", rawsFeatures.size()));
        DefaultTransaction transaction = null;

        try {
            DataStore datastore = dataStoreManager.getInstance();
            SimpleFeatureSource featureSource = datastore.getFeatureSource(rawsFeatureSource);
            if (!(featureSource instanceof SimpleFeatureStore)) {
                logger.error(String.format("Write denied on feature source : %s", featureSource));
                return;
            }

            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
            transaction = new DefaultTransaction("raws-feed-tx");
            featureStore.setTransaction(transaction);
            SimpleFeatureType rawsFeatureType = featureStore.getSchema();
            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(rawsFeatureType);
            List<SimpleFeature> newFeatures = new ArrayList<SimpleFeature>();

            for(RAWSFeature RAWSFeature : rawsFeatures) {
                SimpleFeature simpleFeature = rawsFeatureFactory.buildFeature(RAWSFeature, featureBuilder);
                newFeatures.add(simpleFeature);
                Filter filter = CQL.toFilter("station_id = '" + RAWSFeature.getRawsObservations().getStationId() + "'");
                featureStore.removeFeatures(filter);
                if(newFeatures.size()%100 == 0)
                    logger.debug(String.format("So far processed %d features", newFeatures.size()));
            }
            SimpleFeatureCollection newFeatureCollection = new ListFeatureCollection(rawsFeatureType, newFeatures);
            featureStore.addFeatures(newFeatureCollection);
            transaction.commit();
            logger.info(String.format("Successfully completed processing %d RAWS Features in %d ms", rawsFeatures.size(), stopwatch.elapsed(TimeUnit.MILLISECONDS)));
        } catch(Exception e) {
            logger.error(String.format("Failed to process RAWS features successfully, ran for %d ms before failing", stopwatch.elapsed(TimeUnit.MILLISECONDS)), e);
        } finally {
            if(transaction != null)
                transaction.close();
        }
    }
}