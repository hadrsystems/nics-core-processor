package edu.mit.ll.nics.processor.raws.model;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

public class RAWSFeatureTest {

    @Test
    public void testIfStationIsActive() {
        RAWSFeatureGeometry rawsFeatureGeometry = new RAWSFeatureGeometry("Point", Arrays.asList(-121.0, 36.0));
        RAWSObservations rawsObservations = new RAWSObservations("ACTIVE", "NOT INTERESTING", "NOT INTERESTING VIBES", "CA",
                62.0,10.0,2.0,4.0,238.0,10.0,new Timestamp(new Date().getTime()),"http://test-station.com/more-observations/NOTINTERESTING");
        RAWSFeature rawsFeature = new RAWSFeature(rawsFeatureGeometry, "Feature", rawsObservations);
        Assert.assertTrue(rawsFeature.isStationActive());
    }

    @Test
    public void testIfStationIsActiveIgnoringCase() {
        RAWSFeatureGeometry rawsFeatureGeometry = new RAWSFeatureGeometry("Point", Arrays.asList(-121.0, 36.0));
        RAWSObservations rawsObservations = new RAWSObservations("active", "NOT INTERESTING", "NOT INTERESTING VIBES", "CA",
                62.0,10.0,2.0,4.0,238.0,10.0,new Timestamp(new Date().getTime()),"http://test-station.com/more-observations/NOTINTERESTING");
        RAWSFeature rawsFeature = new RAWSFeature(rawsFeatureGeometry, "Feature", rawsObservations);
        Assert.assertTrue(rawsFeature.isStationActive());
    }

    @Test
    public void testStationsIsInactive() {
        RAWSFeatureGeometry rawsFeatureGeometry = new RAWSFeatureGeometry("Point", Arrays.asList(-121.0, 36.0));
        RAWSObservations rawsObservations = new RAWSObservations("INACTIVE", "NOT INTERESTING", "NOT INTERESTING VIBES", "CA",
                62.0,10.0,2.0,4.0,238.0,10.0,new Timestamp(new Date().getTime()),"http://test-station.com/more-observations/NOTINTERESTING");
        RAWSFeature rawsFeature = new RAWSFeature(rawsFeatureGeometry, "Feature", rawsObservations);
        Assert.assertFalse(rawsFeature.isStationActive());
    }

    @Test
    public void testStationIsInactiveWhenStatusIsNull() {
        RAWSFeatureGeometry rawsFeatureGeometry = new RAWSFeatureGeometry("Point", Arrays.asList(-121.0, 36.0));
        RAWSObservations rawsObservations = new RAWSObservations(null, "NOT INTERESTING", "NOT INTERESTING VIBES", "CA",
                62.0,10.0,2.0,4.0,238.0,10.0,new Timestamp(new Date().getTime()),"http://test-station.com/more-observations/NOTINTERESTING");
        RAWSFeature rawsFeature = new RAWSFeature(rawsFeatureGeometry, "Feature", rawsObservations);
        Assert.assertFalse(rawsFeature.isStationActive());
    }

    @Test
    public void testStationIsInactiveWhenStatusIsEmpty() {
        RAWSFeatureGeometry rawsFeatureGeometry = new RAWSFeatureGeometry("Point", Arrays.asList(-121.0, 36.0));
        RAWSObservations rawsObservations = new RAWSObservations("", "NOT INTERESTING", "NOT INTERESTING VIBES", "CA",
                62.0,10.0,2.0,4.0,238.0,10.0,new Timestamp(new Date().getTime()),"http://test-station.com/more-observations/NOTINTERESTING");
        RAWSFeature rawsFeature = new RAWSFeature(rawsFeatureGeometry, "Feature", rawsObservations);
        Assert.assertFalse(rawsFeature.isStationActive());
    }
}
