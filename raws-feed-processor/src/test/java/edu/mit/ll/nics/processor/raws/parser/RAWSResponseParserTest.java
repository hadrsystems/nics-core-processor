package edu.mit.ll.nics.processor.raws.parser;

import edu.mit.ll.nics.processor.raws.model.*;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RAWSResponseParserTest {

    private final RAWSResponseParser parser = new RAWSResponseParser();

    @Test
    public void rawsResponseWithSummaryParsedSuccessfully() throws IOException {
        RAWSResponse response = parser.parse("{\n" +
                "\"SUMMARY\": {\n" +
                "\"NUMBER_OF_OBJECTS\": 0,\n" +
                "\"RESPONSE_CODE\": -1,\n" +
                "\"RESPONSE_MESSAGE\": \"STATE may only be a two letter abbreviation (ex. UT, CO, WY).\",\n" +
                "\"RESPONSE_TIME\": 0\n" +
                "}\n" +
                "}");
        Assert.assertEquals(response.getRAWSSummary().getNumberOfObjects(), 0);
        Assert.assertEquals(response.getRAWSSummary().getResponseCode(), "-1");
        Assert.assertEquals(response.getRAWSSummary().getResponseMessage(), "STATE may only be a two letter abbreviation (ex. UT, CO, WY).");
        Assert.assertEquals(response.getRAWSSummary().getResponseTime(), 0.0);
    }

    @Test
    public void rawsResponseWithObservationsParsedSuccessfully() throws Exception {
        RAWSResponse response = parser.parse("{\n" +
                "\"UNITS\": {\n" +
                "\"wind_speed\": \"Miles/hour\",\n" +
                "\"air_temp\": \"Fahrenheit\",\n" +
                "\"wind_gust\": \"Miles/hour\",\n" +
                "\"dew_point_temperature\": \"Fahrenheit\",\n" +
                "\"wind_direction\": \"Degrees\",\n" +
                "\"relative_humidity\": \"%\"\n" +
                "},\n" +
                "\"type\": \"FeatureCollection\",\n" +
                "\"features\": [\n" +
                "{\n" +
                "\"geometry\": {\n" +
                "\"type\": \"Point\",\n" +
                "\"coordinates\": [\n" +
                "-116.898889,\n" +
                "34.266111\n" +
                "]\n" +
                "},\n" +
                "\"type\": \"Feature\",\n" +
                "\"properties\": {\n" +
                "\"status\": \"ACTIVE\",\n" +
                "\"mnet_id\": \"2\",\n" +
                "\"date_time\": \"2017-05-05T10:11:00-0700\",\n" +
                "\"elevation\": \"6900\",\n" +
                "\"name\": \"FAWNSKIN\",\n" +
                "\"station_info\": \"http://mesowest.utah.edu/cgi-bin/droman/station_total.cgi?stn=FWSC1\",\n" +
                "\"stid\": \"FWSC1\",\n" +
                "\"longitude\": \"-116.898889\",\n" +
                "\"wind_speed\": 7,\n" +
                "\"relative_humidity\": 23,\n" +
                "\"state\": \"CA\",\n" +
                "\"air_temp\": 75,\n" +
                "\"more_observations\": \"http://mesowest.utah.edu/cgi-bin/droman/meso_base_dyn.cgi?stn=FWSC1\",\n" +
                "\"wind_direction\": 178,\n" +
                "\"latitude\": \" 34.266111\",\n" +
                "\"timezone\": \"America/Los_Angeles\",\n" +
                "\"dew_point_temperature_d\": 34.68,\n" +
                "\"id\": \"1795\",\n" +
                "\"wind_gust\": 13\n" +
                "}\n" +
                "}]}");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date parsedDate = sdf.parse("2017-05-05T10:11:00-0700");
        Assert.assertEquals("FeatureCollection", response.getType());

        //Verify Summary
        Assert.assertNull(response.getRAWSSummary());

        //verify features
        Assert.assertEquals(1, response.getRAWSFeatures().size());
        RAWSFeature RAWSFeature = response.getRAWSFeatures().get(0);

        //verify geometry
        Assert.assertNotNull(RAWSFeature.getRawsFeatureGeometry());
        Assert.assertEquals("Point", RAWSFeature.getRawsFeatureGeometry().getType());
        Assert.assertEquals(-116.898889, RAWSFeature.getRawsFeatureGeometry().getCoordinates().get(0));
        Assert.assertEquals(34.266111, RAWSFeature.getRawsFeatureGeometry().getCoordinates().get(1));

        //verify observations
        RAWSObservations RAWSObservations = RAWSFeature.getRawsObservations();
        Assert.assertNotNull(RAWSObservations);
        Assert.assertEquals("ACTIVE", RAWSObservations.getStatus());
        Assert.assertEquals("FWSC1", RAWSObservations.getStationId());
        Assert.assertEquals("FAWNSKIN", RAWSObservations.getStationName());
        Assert.assertEquals("CA", RAWSObservations.getState());
        Assert.assertEquals(75.0, RAWSObservations.getAirTemperature());
        Assert.assertEquals(34.68, RAWSObservations.getDewPointTemperature());
        Assert.assertEquals(7.0, RAWSObservations.getWindSpeed());
        Assert.assertEquals(13.0, RAWSObservations.getWindGust());
        Assert.assertEquals(178.0, RAWSObservations.getWindDirection());
        Assert.assertEquals(23.0, RAWSObservations.getRelativeHumidity());
        Assert.assertEquals(parsedDate, RAWSObservations.getLastObservationAt());
        Assert.assertEquals("http://mesowest.utah.edu/cgi-bin/droman/meso_base_dyn.cgi?stn=FWSC1", RAWSObservations.getMoreObservationsUrl());

        //verify Units
        RAWSUnits RAWSUnits = response.getRAWSUnits();
        Assert.assertNotNull(RAWSUnits);
        Assert.assertEquals(RAWSUnits.getAirTemperature(), "Fahrenheit");
        Assert.assertEquals(RAWSUnits.getDewPointTemperature(), "Fahrenheit");
        Assert.assertEquals(RAWSUnits.getWindSpeed(), "Miles/hour");
        Assert.assertEquals(RAWSUnits.getWindGust(), "Miles/hour");
        Assert.assertEquals(RAWSUnits.getWindDirection(), "Degrees");
        Assert.assertEquals(RAWSUnits.getRelativeHumidity(), "%");
    }

    @Test
    public void testEmptyObservationsResultInNulls() throws IOException {
        RAWSResponse response = parser.parse("{\n" +
                "\"UNITS\": {\n" +
                "\"wind_speed\": \"Miles/hour\",\n" +
                "\"air_temp\": \"Fahrenheit\",\n" +
                "\"wind_gust\": \"Miles/hour\",\n" +
                "\"dew_point_temperature\": \"Fahrenheit\",\n" +
                "\"wind_direction\": \"Degrees\",\n" +
                "\"relative_humidity\": \"%\"\n" +
                "},\n" +
                "\"type\": \"FeatureCollection\",\n" +
                "\"features\": [\n" +
                "{\n" +
                "\"geometry\": {\n" +
                "\"type\": \"Point\",\n" +
                "\"coordinates\": [\n" +
                "-116.898889,\n" +
                "34.266111\n" +
                "]\n" +
                "},\n" +
                "\"type\": \"Feature\",\n" +
                "\"properties\": {\n" +
                "\"status\": \"ACTIVE\",\n" +
                "\"mnet_id\": \"2\",\n" +
                "\"date_time\": \"2017-05-05T10:11:00-0700\",\n" +
                "\"elevation\": \"6900\",\n" +
                "\"name\": \"FAWNSKIN\",\n" +
                "\"station_info\": \"http://mesowest.utah.edu/cgi-bin/droman/station_total.cgi?stn=FWSC1\",\n" +
                "\"stid\": \"FWSC1\",\n" +
                "\"longitude\": \"-116.898889\",\n" +
                "\"wind_speed\": 7,\n" +
                "\"relative_humidity\": null,\n" +
                "\"state\": \"CA\",\n" +
                "\"more_observations\": \"http://mesowest.utah.edu/cgi-bin/droman/meso_base_dyn.cgi?stn=FWSC1\",\n" +
                "\"wind_direction\": 178,\n" +
                "\"latitude\": \" 34.266111\",\n" +
                "\"timezone\": \"America/Los_Angeles\",\n" +
                "\"dew_point_temperature_d\": 34.68,\n" +
                "\"id\": \"1795\",\n" +
                "\"wind_gust\": 13\n" +
                "}\n" +
                "}]}");

        //verify features
        Assert.assertEquals(1, response.getRAWSFeatures().size());
        RAWSFeature RAWSFeature = response.getRAWSFeatures().get(0);

        //verify observations
        RAWSObservations RAWSObservations = RAWSFeature.getRawsObservations();
        Assert.assertNotNull(RAWSObservations);
        Assert.assertEquals("CA", RAWSObservations.getState());
        Assert.assertNull(RAWSObservations.getAirTemperature());
        Assert.assertNull(RAWSObservations.getRelativeHumidity());
    }

    @Test
    public void testEmptyResponse() throws Exception {
        String testJson = "{\n" +
                "\"UNITS\": {},\n" +
                "\"type\": \"FeatureCollection\",\n" +
                "\"features\": []\n" +
                "}";
        RAWSResponse parsedRespone = parser.parse(testJson);
        Assert.assertNotNull(parsedRespone);
        RAWSUnits units = parsedRespone.getRAWSUnits();
        Assert.assertNotNull(units);
        Assert.assertNull(units.getAirTemperature());
        Assert.assertNull(units.getDewPointTemperature());
        Assert.assertNull(units.getRelativeHumidity());
        Assert.assertNull(units.getWindDirection());
        Assert.assertNull(units.getWindGust());
        Assert.assertNull(units.getWindSpeed());

        Assert.assertNull(parsedRespone.getRAWSSummary());

        Assert.assertEquals(parsedRespone.getType(), "FeatureCollection");

        Assert.assertNotNull(parsedRespone.getRAWSFeatures());
        Assert.assertEquals(parsedRespone.getRAWSFeatures().size(), 0);
    }
}
