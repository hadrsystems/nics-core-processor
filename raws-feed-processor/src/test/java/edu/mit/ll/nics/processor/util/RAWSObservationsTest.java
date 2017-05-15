package edu.mit.ll.nics.processor.util;

import edu.mit.ll.nics.processor.raws.model.RAWSObservations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class RAWSObservationsTest {
    private static final SimpleDateFormat simpleDateFormatInPDT = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
    private static final SimpleDateFormat timeFormatInUTC = new SimpleDateFormat("HH:mm z");

    @BeforeClass
    public void setupClass() {
        simpleDateFormatInPDT.setTimeZone(TimeZone.getTimeZone("PST"));
        timeFormatInUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Test
    public void testDescription() {
        RAWSObservations rawsObservations = new RAWSObservations("ACTIVE", "SNVC", "SAMPLE NONE VAN CAR", "CA", 92.5, 78.0, 12.0,4.0,134.0,10.0,new Timestamp(new Date().getTime()), "http://test-site-with-more-observations");
        Assert.assertEquals(rawsObservations.getDescription(), this.getDescription(rawsObservations));
    }

    @Test
    public void testDescriptionWithNulls() {
        RAWSObservations rawsObservations = new RAWSObservations("INACTIVE", "SNVC", "SAMPLE NONE VAN CAR", "CA", null, null, null,null,null,null,new Timestamp(new Date().getTime()), "http://test-site-with-more-observations");
        Assert.assertEquals(rawsObservations.getDescription(), this.getDescription(rawsObservations));
    }

    private String getDescription(RAWSObservations rawsFeature) {
        StringBuilder description = new StringBuilder(String.format("<br><b>%s</b> %s %s<br>", rawsFeature.getStationName(), rawsFeature.getStationId(), rawsFeature.getStatus()));
        description.append(String.format("<b>%s  %s</b><br>", simpleDateFormatInPDT.format(rawsFeature.getLastObservationAt()), timeFormatInUTC.format(rawsFeature.getLastObservationAt())));
        description.append(String.format("<b>Wind:</b>                  %s %s MPH<br>", rawsFeature.getCompassDirection(), (rawsFeature.getWindSpeed() == null) ? "N/A" : rawsFeature.getWindSpeed().toString()));
        description.append(String.format("<b>Peak Gust:</b>                  %s MPH<br>", (rawsFeature.getWindGust() == null) ? "N/A" : rawsFeature.getWindGust().toString()));
        description.append(String.format("<b>Temperature:</b>           %s &#8457;<br>", (rawsFeature.getAirTemperature() == null) ? "N/A" : rawsFeature.getAirTemperature().toString()));
        description.append(String.format("<b>Dew Point:</b>             %s &#8457;<br>", (rawsFeature.getDewPointTemperature() == null) ? "N/A" : rawsFeature.getDewPointTemperature().toString()));
        description.append(String.format("<b>Humidity:</b>              %s &#37;<br>", (rawsFeature.getRelativeHumidity() == null) ? "N/A" : rawsFeature.getRelativeHumidity().toString()));
        return description.toString();
    }
}
