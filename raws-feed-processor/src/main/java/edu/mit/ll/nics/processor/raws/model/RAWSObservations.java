package edu.mit.ll.nics.processor.raws.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.mit.ll.nics.processor.util.CompassDirectionConverter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
    private static final SimpleDateFormat timeFormatInUTC = new SimpleDateFormat("HH:mm z");
    private static final CompassDirectionConverter compassDirectionConverter = new CompassDirectionConverter();

    static{
        timeFormatInUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public RAWSObservations() {}

    public RAWSObservations(String status, String stationId, String stationName, String state, Double airTemperature, Double dewPointTemperature,
                            Double windSpeed, Double windGust, Double windDirection, Double relativeHumidity, Timestamp lastObservationAt, String moreObservationsUrl) {
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

    public String getCompassDirection() {
        return this.getWindDirection() == null ? "" : compassDirectionConverter.getCompassDirection(this.getWindDirection());
    }

    public String getDescription() {
        StringBuilder description = new StringBuilder(String.format("<br><b>%s</b> %s %s<br>", this.getStationName(), this.getStationId(), this.getStatus()));
        description.append(String.format("<b>%s  %s</b><br>", simpleDateFormat.format(this.getLastObservationAt()), timeFormatInUTC.format(this.getLastObservationAt())));
        description.append(String.format("<b>Wind:                  %s %s MPH</b><br>", this.getCompassDirection(), (this.getWindSpeed() == null) ? "N/A" : this.getWindSpeed().toString()));
        description.append(String.format("<b>Wind Gust:                  %s MPH</b><br>", (this.getWindGust() == null) ? "N/A" : this.getWindGust().toString()));
        description.append(String.format("<b>Temperature:           %s &#8457;</b><br>", (this.getAirTemperature() == null) ? "N/A" : this.getAirTemperature().toString()));
        description.append(String.format("<b>Dew Point:             %s &#8457;</b><br>", (this.getDewPointTemperature() == null) ? "N/A" : this.getDewPointTemperature().toString()));
        description.append(String.format("<b>Humidity:              %s &#37; </b><br>", (this.getRelativeHumidity() == null) ? "N/A" : this.getRelativeHumidity().toString()));
        return description.toString();
    }

    public String toString() {
        return String.format("[Station Id: %s, Station Name: %s, Station Status: %s, State: %s, Air Temperature: %f, Dew Point: %f, " +
                "Wind Speed: %f, Wind Gust: %f, Wind Direction: %f, Relative Humidity: %f]", this.getStationId(), this.getStationName(), this.getStatus(), this.getState(),
                                        this.getAirTemperature(), this.getDewPointTemperature(), this.getWindSpeed(), this.getWindGust(), this.getWindDirection(), this.getRelativeHumidity());
    }
}
