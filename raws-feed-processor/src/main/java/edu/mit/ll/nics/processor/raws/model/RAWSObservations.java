package edu.mit.ll.nics.processor.raws.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RAWSObservations {
    private String status;
    @JsonProperty("stid")
    private String stationId;
    @JsonProperty("name")
    private String stationName;
    private String state;
    @JsonProperty("air_temp")
    private Double airTemperature;
    @JsonProperty("dew_point_temperature_d")
    private Double dewPointTemperature;
    @JsonProperty("wind_speed")
    private Double windSpeed;
    @JsonProperty("wind_gust")
    private Double windGust;
    @JsonProperty("wind_direction")
    private Double windDirection;
    @JsonProperty("relative_humidity")
    private Double relativeHumidity;
    @JsonProperty("date_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date lastObservationAt;
    @JsonProperty("more_observations")
    private String moreObservationsUrl;

    public RAWSObservations() {}

    public RAWSObservations(String status, String stationId, String stationName, String state, double airTemperature, double dewPointTemperature,
                            double windSpeed, double windGust, double windDirection, double relativeHumidity, Timestamp lastObservationAt, String moreObservationsUrl) {
        this.status = status;
        this.stationId = stationId;
        this.stationName = stationName;
        this.state = state;
        this.airTemperature = airTemperature;
        this.dewPointTemperature = dewPointTemperature;
        this.windSpeed = windSpeed;
        this.windGust = windGust;
        this.windDirection = windDirection;
        this.relativeHumidity = relativeHumidity;
        this.lastObservationAt = lastObservationAt;
        this.moreObservationsUrl = moreObservationsUrl;
    }

    public String getStatus() {
        return status;
    }

    public String getStationId() {
        return stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public String getState() {
        return state;
    }

    public Double getAirTemperature() {
        return airTemperature;
    }

    public Double getDewPointTemperature() {
        return dewPointTemperature;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public Double getWindGust() {
        return windGust;
    }

    public Double getWindDirection() {
        return windDirection;
    }

    public Double getRelativeHumidity() {
        return relativeHumidity;
    }

    public Date getLastObservationAt() {
        return lastObservationAt;
    }

    public String getMoreObservationsUrl() {
        return moreObservationsUrl;
    }

    public String getDescription() {
        return "";
    }
}
