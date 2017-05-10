package edu.mit.ll.nics.processor.raws.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class RAWSUnits {
    @JsonProperty("air_temp")
    private String airTemperature;
    @JsonProperty("dew_point_temperature")
    private String dewPointTemperature;
    @JsonProperty("wind_speed")
    private String windSpeed;
    @JsonProperty("wind_gust")
    private String windGust;
    @JsonProperty("wind_direction")
    private String windDirection;
    @JsonProperty("relative_humidity")
    private String relativeHumidity;

    public RAWSUnits() {
    }

    public String getAirTemperature() {
        return airTemperature;
    }

    public String getDewPointTemperature() {
        return dewPointTemperature;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public String getWindGust() {
        return windGust;
    }

    public String getRelativeHumidity() {
        return relativeHumidity;
    }

    public String toString() {
        return String.format("[airTemperature: %s, dewPointTemperature: %s, windSpeed: %s, windGust: %s, windDirection: %s, relativeHumidity: %s]",
                airTemperature, dewPointTemperature, windSpeed, windGust, windDirection, relativeHumidity);
    }
}
