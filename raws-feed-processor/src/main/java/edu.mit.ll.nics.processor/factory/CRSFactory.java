package edu.mit.ll.nics.processor.factory;

import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class CRSFactory {

    public CoordinateReferenceSystem buildCRS(String crsString) throws FactoryException {
        if("WGS84".equalsIgnoreCase(crsString))
            return DefaultGeographicCRS.WGS84;
        else
            return CRS.decode(crsString);
    }
}
