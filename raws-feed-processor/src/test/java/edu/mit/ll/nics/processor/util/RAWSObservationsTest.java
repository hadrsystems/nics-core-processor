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
        RAWSObservations rawsObservations = new RAWSObservations("ACTIVE", "SNVC", "SAMPLE NONE VAN CAR", "CA", 92.5, 78.9, 12.1,4.0,134.0,10.0,new Timestamp(new Date().getTime()), "http://test-site-with-more-observations");
        System.out.println(rawsObservations.getDescription());
        Assert.assertEquals(rawsObservations.getDescription(), this.getDescription(rawsObservations));
    }

    @Test
    public void testDescriptionWithNulls() {
        RAWSObservations rawsObservations = new RAWSObservations("INACTIVE", "SNVC", "SAMPLE NONE VAN CAR", "CA", null, null, null,null,null,null,new Timestamp(new Date().getTime()), "http://test-site-with-more-observations");
        System.out.println(rawsObservations.getDescription());
        Assert.assertEquals(rawsObservations.getDescription(), this.getDescription(rawsObservations));
    }

    private String getDescription(RAWSObservations rawsObservations) {
        StringBuilder description = new StringBuilder(String.format("<br><b>%s</b> %s %s<br>", rawsObservations.getStationName(), rawsObservations.getStationId(), rawsObservations.getStatus()));
        description.append(String.format("<b>%s  %s</b><br>", simpleDateFormatInPDT.format(rawsObservations.getLastObservationAt()), timeFormatInUTC.format(rawsObservations.getLastObservationAt())));
        description.append(String.format("<b>Wind:</b>                  %s %s<br>", rawsObservations.getCompassDirection(), this.getWindSpeedForDescription(rawsObservations.getWindSpeed())));
        description.append(String.format("<b>Peak Gust:</b>                  %s<br>", this.getWindGustForDescription(rawsObservations.getWindGust())));
        description.append(String.format("<b>Temperature:</b>           %s<br>", this.getAirTemperatureForDescription(rawsObservations.getAirTemperature())));
        description.append(String.format("<b>Dew Point:</b>             %s<br>", this.getDewPointTemperatureForDescription(rawsObservations.getDewPointTemperature())));
        description.append(String.format("<b>Humidity:</b>              %s<br>", this.getRelativeHumidityForDescription(rawsObservations.getRelativeHumidity())));
        return description.toString();
    }

    private String getWindSpeedForDescription(Double windSpeed) {
        return (windSpeed == null) ? "N/A" : Long.toString(Math.round(windSpeed)) + " MPH";
    }

    private String getWindGustForDescription(Double windGust) {
        return (windGust == null) ? "N/A" : Long.toString(Math.round(windGust)) + " MPH";
    }

    private String getAirTemperatureForDescription(Double airTemperature) {
        return (airTemperature == null) ? "N/A" : Long.toString(Math.round(airTemperature)) + " &#8457;";
    }

    private String getDewPointTemperatureForDescription(Double dewPointTemperature) {
        return (dewPointTemperature == null) ? "N/A" : Long.toString(Math.round(dewPointTemperature)) + " &#8457;";
    }

    private String getRelativeHumidityForDescription(Double relativeHumidity) {
        return (relativeHumidity == null) ? "N/A" : Long.toString(Math.round(relativeHumidity)) + " &#37;";
    }

}
