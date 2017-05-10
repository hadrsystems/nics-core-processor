package edu.mit.ll.nics.processor.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CompassDirectionConverterTest {

    private CompassDirectionConverter converter = new CompassDirectionConverter();

    @Test
    public void testReturnsNorthForNorthDirectionInDegrees() {
        double directionInDegrees = 0;
        Assert.assertEquals(converter.getCompassDirection(directionInDegrees), "N");
        Assert.assertEquals(converter.getCompassDirection(20), "N");
    }

    @Test
    public void testReturnsNNE() {
        Assert.assertEquals(converter.getCompassDirection(22.5), "NNE");
        Assert.assertEquals(converter.getCompassDirection(42.5), "NNE");
        Assert.assertEquals(converter.getCompassDirection(44.8), "NNE");
    }

    @Test
    public void testDegreesAreNormalizedBeforeCalculatingCompassDirection() {
        Assert.assertEquals(converter.getCompassDirection(360+360), "N");
        Assert.assertEquals(converter.getCompassDirection(360+250), "WSW");
        Assert.assertEquals(converter.getCompassDirection(360+170), "SSE");
    }
}
