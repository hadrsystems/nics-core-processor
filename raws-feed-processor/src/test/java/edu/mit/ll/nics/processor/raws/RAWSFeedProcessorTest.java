package edu.mit.ll.nics.processor.raws;

import edu.mit.ll.nics.processor.DataStoreManager;
import edu.mit.ll.nics.processor.factory.RAWSFeatureFactory;
import edu.mit.ll.nics.processor.raws.model.*;
import edu.mit.ll.nics.processor.raws.parser.RAWSResponseParser;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.text.cql2.CQL;
import org.mockito.Mockito;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;

public class RAWSFeedProcessorTest {

    private DataStoreManager dataStoreManager;
    private final String rawsFeatureSource = "raws";
    private final RAWSResponseParser rawsResponseParser = mock(RAWSResponseParser.class);
    private final RAWSFeatureFactory rawsFeatureFactory = mock(RAWSFeatureFactory.class);

    private final DataStore dataStore = mock(DataStore.class);
    private final SimpleFeatureSource featureSourceReadOnly = mock(SimpleFeatureSource.class);
    private final SimpleFeatureStore featureStore = mock(SimpleFeatureStore.class);
    private final SimpleFeatureType rawsFeatureType = mock(SimpleFeatureType.class);
    private final SimpleFeatureBuilder simpleFeatureBuilder = mock(SimpleFeatureBuilder.class);
    private final SimpleFeature simpleFeature1 = mock(SimpleFeature.class);

    private RAWSFeedProcessor rawsFeedProcessor;
    private final Exchange exchange = mock(Exchange.class);
    private final Message message = mock(Message.class);
    private final String testGeoJson = "Test Geo Json";

    private RAWSResponse response = mock(RAWSResponse.class);
    private final RAWSSummary summary = mock(RAWSSummary.class);

    @BeforeMethod
    public void setup() throws Exception {
        dataStoreManager = mock(DataStoreManager.class);
        response = mock(RAWSResponse.class);
        rawsFeedProcessor = new RAWSFeedProcessor(dataStoreManager, rawsFeatureSource, rawsResponseParser, rawsFeatureFactory);
        when(exchange.getIn()).thenReturn(message);
        when(message.getBody(String.class)).thenReturn(testGeoJson);
        when(rawsResponseParser.parse(testGeoJson)).thenReturn(response);
    }

    @Test
    public void testWhenResponseHasErrors() throws Exception {
        when(response.hasErrors()).thenReturn(true);
        when(response.getRAWSSummary()).thenReturn(summary);
        when(summary.getResponseMessage()).thenReturn("Test error");
        rawsFeedProcessor.process(exchange);
        verify(response, never()).getRAWSFeatures();
        verify(dataStoreManager, never()).getInstance();
    }

    @Test
    public void testWhenResponseHasZeroResults() throws Exception {
        when(response.hasErrors()).thenReturn(false);
        when(response.getRAWSFeatures()).thenReturn(Collections.EMPTY_LIST);
        rawsFeedProcessor.process(exchange);
        verify(dataStoreManager, never()).getInstance();
    }

    @Test
    public void testWhenFeatureSourceIsNotWritable() throws Exception {
        when(response.hasErrors()).thenReturn(false);
        when(response.getRAWSFeatures()).thenReturn(Arrays.asList(mock(RAWSFeature.class)));
        when(dataStoreManager.getInstance()).thenReturn(dataStore);
        when(dataStore.getFeatureSource(rawsFeatureSource)).thenReturn(featureSourceReadOnly);
        rawsFeedProcessor.process(exchange);
        Mockito.verifyZeroInteractions(featureSourceReadOnly);
    }

    @Test
    public void testWhenResponseHasValidFeaturesAndFeatureSourceIsWritable() throws Exception {
        RAWSFeatureGeometry rawsFeatureGeometry1 = new RAWSFeatureGeometry("Point", Arrays.asList(-121.0, 36.0));
        RAWSObservations rawsObservations1 = new RAWSObservations("ACTIVE", "POSITIVE", "POSITIVE VIBES", "CA",
                62.0,10.0,2.0,4.0,238.0,10.0,new Timestamp(new Date().getTime()),"http://test-station.com/more-observations/POSITIVE");
        RAWSFeature rawsFeature1 = new RAWSFeature(rawsFeatureGeometry1, "Feature", rawsObservations1);

//        RAWSFeatureGeometry rawsFeatureGeometry2 = new RAWSFeatureGeometry("Point", Arrays.asList(-100.0, 12.0));
//        RAWSObservations rawsObservations2 = new RAWSObservations("INACTIVE", "HAPPY", "HAPPY VIBES", "CA",
//                12,11,1,0,18,7,new Timestamp(new Date().getTime()),"http://test-station.com/more-observations/HAPPY");
//        RAWSFeature rawsFeature2 = new RAWSFeature(rawsFeatureGeometry2, rawsObservations2);
        List<RAWSFeature> rawsFeatures = Arrays.asList(rawsFeature1);//, rawsFeature2);
        RAWSUnits units = new RAWSUnits();
        Filter filter = CQL.toFilter("station_id = '" + rawsFeature1.getRawsObservations().getStationId() + "'");

        when(response.hasErrors()).thenReturn(false);
        when(response.getRAWSFeatures()).thenReturn(rawsFeatures);
        when(dataStoreManager.getInstance()).thenReturn(dataStore);
        when(dataStore.getFeatureSource(rawsFeatureSource)).thenReturn(featureStore);
        when(featureStore.getSchema()).thenReturn(rawsFeatureType);
        when(rawsFeatureFactory.buildFeature(eq(rawsFeature1), any(SimpleFeatureBuilder.class))).thenReturn(simpleFeature1);
        rawsFeedProcessor.process(exchange);
        verify(featureStore).setTransaction(any(DefaultTransaction.class));
        verify(featureStore).removeFeatures(eq(filter));
        verify(featureStore).addFeatures(any(ListFeatureCollection.class));
    }
}
