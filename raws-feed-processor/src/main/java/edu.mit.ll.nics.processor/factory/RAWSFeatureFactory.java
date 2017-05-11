package edu.mit.ll.nics.processor.factory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import edu.mit.ll.nics.processor.raws.model.RAWSFeature;
import edu.mit.ll.nics.processor.raws.model.RAWSFeatureGeometry;
import edu.mit.ll.nics.processor.raws.model.RAWSObservations;
import edu.mit.ll.nics.processor.util.Clock;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.sql.Timestamp;

public class RAWSFeatureFactory {

    private final GeometryFactory geometryFactory;
    private final CoordinateReferenceSystem sourceCRS;
    private final CoordinateReferenceSystem targetCRS;
    private final Clock clock;

    public RAWSFeatureFactory(GeometryFactory geometryFactory, Clock clock, CRSFactory crsFactory, String rawsSourceCRSString, String rawsTargetCRSString) throws FactoryException {
        this.geometryFactory = geometryFactory;
        this.clock = clock;
        this.sourceCRS = crsFactory.buildCRS(rawsSourceCRSString);
        this.targetCRS = crsFactory.buildCRS(rawsTargetCRSString);
    }

    public SimpleFeature buildFeature(RAWSFeature sourceRAWSFeature, SimpleFeatureBuilder featureBuilder) throws Exception {
        RAWSObservations rawsObservations = sourceRAWSFeature.getRawsObservations();
        Timestamp currentTimestamp = getCurrentTimestamp();
        Point point = createGeom(sourceRAWSFeature.getRawsFeatureGeometry());
        return featureBuilder.buildFeature("1", new Object[]{
                rawsObservations.getStationId(), rawsObservations.getStationName(), rawsObservations.getStatus(), rawsObservations.getState(),
                rawsObservations.getAirTemperature(), rawsObservations.getWindSpeed(), rawsObservations.getWindDirection(),
                rawsObservations.getWindGust(), rawsObservations.getDewPointTemperature(), rawsObservations.getRelativeHumidity(),
                rawsObservations.getMoreObservationsUrl(), rawsObservations.getDescription(), new Timestamp(rawsObservations.getLastObservationAt().getTime()), currentTimestamp, point
        });
    }

    private com.vividsolutions.jts.geom.Point createGeom(RAWSFeatureGeometry RAWSFeatureGeometry) throws FactoryException, MismatchedDimensionException, TransformException {
        Coordinate pointCoordinates = new Coordinate(RAWSFeatureGeometry.getCoordinates().get(0), RAWSFeatureGeometry.getCoordinates().get(1));
        Point point = geometryFactory.createPoint(pointCoordinates);
        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, false);
        return (Point) JTS.transform(point, transform);
    }

    private Timestamp getCurrentTimestamp() {
        return new Timestamp(clock.getCurrentDate().getTime());
    }
}
