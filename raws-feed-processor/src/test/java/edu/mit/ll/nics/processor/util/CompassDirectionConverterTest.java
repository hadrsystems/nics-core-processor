package edu.mit.ll.nics.processor.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CompassDirectionConverterTest {

    private final CompassDirectionConverter converter = new CompassDirectionConverter();

    @Test
    public void testReturnsNorthForNorthDirectionInDegrees() {
        double directionInDegrees = 0;
        Assert.assertEquals(converter.getCompassDirection(directionInDegrees), "N");
        Assert.assertEquals(converter.getCompassDirection(20), "NNE");
    }

    @Test
    public void verifyDirectionDegreesForNorth() {
        Assert.assertEquals(converter.getCompassDirection(0), "N");
        Assert.assertEquals(converter.getCompassDirection(10), "N");
        Assert.assertEquals(converter.getCompassDirection(360), "N");
    }

    @Test
    public void verifyDirectionDegreesForNNE() {
        Assert.assertEquals(converter.getCompassDirection(12.25), "NNE");
        Assert.assertEquals(converter.getCompassDirection(12.26), "NNE");
        Assert.assertEquals(converter.getCompassDirection(20), "NNE");
        Assert.assertEquals(converter.getCompassDirection(22.5), "NNE");
    }

    @Test
    public void verifyDirectionDegreesForNE() {
        Assert.assertEquals(converter.getCompassDirection(34.75), "NE");
        Assert.assertEquals(converter.getCompassDirection(42.5), "NE");
        Assert.assertEquals(converter.getCompassDirection(44.8), "NE");
    }

    @Test
    public void verifyCompassDirectionForSSW() {
        Assert.assertEquals(converter.getCompassDirection(202.0), "SSW");
    }

    @Test
    public void testDegreesAreNormalizedBeforeCalculatingCompassDirection() {
        Assert.assertEquals(converter.getCompassDirection(360+360), "N");
        Assert.assertEquals(converter.getCompassDirection(360+250), "WSW");
        Assert.assertEquals(converter.getCompassDirection(360+170), "S");
    }
}
