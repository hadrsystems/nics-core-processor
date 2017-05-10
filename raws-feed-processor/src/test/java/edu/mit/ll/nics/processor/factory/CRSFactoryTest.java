package edu.mit.ll.nics.processor.factory;

import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CRSFactoryTest {
    private CRSFactory crsFactory = new CRSFactory();

    @Test
    public void testReturnsValidWGS84() throws FactoryException {
        CoordinateReferenceSystem crs = crsFactory.buildCRS("WGS84");
        Assert.assertNotNull(crs);
        Assert.assertEquals(crs, DefaultGeographicCRS.WGS84);
    }

    @Test
    public void testReturnsValid3857() throws FactoryException {
        CoordinateReferenceSystem crs = crsFactory.buildCRS("EPSG:3857");
        Assert.assertNotNull(crs);
        Assert.assertTrue(crs.getIdentifiers().iterator().next().toString().equals("EPSG:3857"));
    }
}
