package edu.mit.ll.nics.processor.raws;

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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class RAWSFeedProcessor implements Processor {

    private static final Logger logger = Logger.getLogger(RAWSFeedProcessor.class.getSimpleName());
    private DataStoreManager dataStoreManager;
    private String rawsFeatureSource;
    private RAWSResponseParser rawsResponseParser;
    private RAWSFeatureFactory rawsFeatureFactory;

    public RAWSFeedProcessor(DataStoreManager dataStoreManager, String rawsFeatureSource, RAWSResponseParser rawsResponseParser,
                             RAWSFeatureFactory rawsFeatureFactory) throws IOException, FactoryException {
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
            }
            SimpleFeatureCollection newFeatureCollection = new ListFeatureCollection(rawsFeatureType, newFeatures);
            featureStore.addFeatures(newFeatureCollection);
            transaction.commit();
            logger.info(String.format("Successfully completed processing %d RAWS Features", rawsFeatures.size()));
        } catch(Exception e) {
            logger.error("Error processing RAWS features", e);
            return;
        } finally {
            if(transaction != null)
                transaction.close();
        }
    }

    private static Timestamp decodeObservation(String lastObservationStr)  throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date parsedDate = sdf.parse(lastObservationStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parsedDate);
        return new Timestamp(calendar.getTimeInMillis());
    }
}