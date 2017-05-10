package edu.mit.ll.nics.processor.raws.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.mit.ll.nics.processor.util.CompassDirectionConverter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
    private static CompassDirectionConverter compassDirectionConverter = new CompassDirectionConverter();

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
        StringBuilder description = new StringBuilder();
        String compassDirection = this.getWindDirection() == null ? "" : compassDirectionConverter.getCompassDirection(this.getWindDirection());
        String windSpeed = (this.getWindSpeed() == null) ? "N/A" : this.getWindSpeed().toString();
        String windGust = (this.getWindGust() == null) ? "N/A" : this.getWindGust().toString();
        String airTemperature = (this.getAirTemperature() == null) ? "N/A" : this.getAirTemperature().toString();
        String dewPoint = (this.getDewPointTemperature() == null) ? "N/A" : this.getDewPointTemperature().toString();
        String humidity = (this.getRelativeHumidity() == null) ? "N/A" : this.getRelativeHumidity().toString();
        description.append(String.format("<br><b>%s</b> %s %s<br>", this.getStationName(), this.getStationId(), this.getStatus()));
        description.append(String.format("<b>%s</b><br>", simpleDateFormat.format(this.getLastObservationAt())));
        description.append(String.format("<b>Wind:                  %s %s MPH</b><br>", compassDirection, windSpeed));
        description.append(String.format("<b>Wind Gust:                  %s MPH</b><br>", windGust));
        description.append(String.format("<b>Temperature:           %s &#8457;</b><br>", airTemperature));
        description.append(String.format("<b>Dew Point:             %s &#8457;</b><br>", dewPoint));
        description.append(String.format("<b>Humidity:              %s &#37; </b><br>", humidity));
        return description.toString();
    }
}
