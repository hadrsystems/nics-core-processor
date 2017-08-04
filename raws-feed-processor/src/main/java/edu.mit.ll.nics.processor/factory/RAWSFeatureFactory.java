package edu.mit.ll.nics.processor.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import edu.mit.ll.nics.processor.raws.model.RAWSFeature;
import edu.mit.ll.nics.processor.raws.model.RAWSFeatureGeometry;
import edu.mit.ll.nics.processor.raws.model.RAWSObservations;
import edu.mit.ll.nics.processor.raws.model.RAWSQCSummary;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class RAWSFeatureFactory {

    private final GeometryFactory geometryFactory;
    private final CoordinateReferenceSystem sourceCRS;
    private final CoordinateReferenceSystem targetCRS;
    private final Clock clock;
    private enum QCStatus {OK, WARNING, ERROR}
    
    public RAWSFeatureFactory(GeometryFactory geometryFactory, Clock clock, CRSFactory crsFactory, String rawsSourceCRSString, String rawsTargetCRSString) throws FactoryException {
        this.geometryFactory = geometryFactory;
        this.clock = clock;
        this.sourceCRS = crsFactory.buildCRS(rawsSourceCRSString);
        this.targetCRS = crsFactory.buildCRS(rawsTargetCRSString);
    }

    public SimpleFeature buildFeature(RAWSFeature sourceRAWSFeature, SimpleFeatureBuilder featureBuilder, RAWSQCSummary qcSummary) throws Exception {
        RAWSObservations rawsObservations = sourceRAWSFeature.getRawsObservations();
        Timestamp currentTimestamp = getCurrentTimestamp();
        Point point = createGeom(sourceRAWSFeature.getRawsFeatureGeometry());
        QCStatus qcStatus = QCStatus.OK;

        if (qcSummary != null && rawsObservations.getQcFlagged() == true) {
             qcStatus = buildQcStatus(qcSummary.getShortNames(),sourceRAWSFeature);
        }
        return featureBuilder.buildFeature("1", new Object[]{
                rawsObservations.getStationId(), rawsObservations.getStationName(), rawsObservations.getStatus(), rawsObservations.getState(),
                rawsObservations.getAirTemperature(), rawsObservations.getWindSpeed(), rawsObservations.getWindDirection(),
                rawsObservations.getWindGust(), rawsObservations.getDewPointTemperature(), rawsObservations.getRelativeHumidity(),
                rawsObservations.getMoreObservationsUrl(), buildDescription(rawsObservations.getDescription(),rawsObservations.getMoreObservationsUrl()), new Timestamp(rawsObservations.getLastObservationAt().getTime()),
                currentTimestamp, point, qcStatus.name()
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

    private QCStatus buildQcStatus(HashMap<Integer,String> qcNameLookup, RAWSFeature rawsFeature) {
        RAWSObservations observations = rawsFeature.getRawsObservations();
        HashMap<String,Integer[]> qcData = observations.getQcData();
        if (!observations.getQcFlagged() || qcData == null || qcData.isEmpty()) {
            return QCStatus.OK;
        }
        //Build a single status from custom logic. Error: an sl_range_check, Warning: Anything Else.
        for (Integer[] qcStats : qcData.values()) {
            for(Integer qcStatCode: qcStats) {
                String qcName = qcNameLookup.get(qcStatCode);
                if (qcName.equalsIgnoreCase("sl_range_check")) {
                    return QCStatus.ERROR;
                }
            }

        }
        return QCStatus.WARNING;

    }

    private String buildDescription(String featureDesc, String moreObservationsUrl)
    {
        return featureDesc + String.format("<br/><a href=\"%s\">More Information</a>",moreObservationsUrl);


    }

}
