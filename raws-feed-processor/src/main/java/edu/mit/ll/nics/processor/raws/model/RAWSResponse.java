package edu.mit.ll.nics.processor.raws.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RAWSResponse {

    protected static final String RESPONSE_CODE_NO_ERRORS = "1";
    protected static final String RESPONSE_CODE_ZERO_RESULTS = "2";
    protected static final String RESPONSE_CODE_AUTHENTICATION_ERROR = "200";
    protected static final String RESPONSE_CODE_VALIDATION_ERROR = "-1";

    @JsonProperty("SUMMARY")
    private RAWSSummary RAWSSummary;
    @JsonProperty("UNITS")
    private RAWSUnits RAWSUnits;
    private String type;
    @JsonProperty("features")
    private List<RAWSFeature> RAWSFeatures;

    public RAWSResponse() {
    }

    protected RAWSResponse(RAWSSummary RAWSSummary, RAWSUnits RAWSUnits, String type, List<RAWSFeature> RAWSFeatures) {
        this.RAWSSummary = RAWSSummary;
        this.RAWSUnits = RAWSUnits;
        this.type = type;
        this.RAWSFeatures = RAWSFeatures;
    }

    public RAWSSummary getRAWSSummary() {
        return RAWSSummary;
    }

    public RAWSUnits getRAWSUnits() {
        return RAWSUnits;
    }

    public String getType() {
        return type;
    }

    public List<RAWSFeature> getRAWSFeatures() { return RAWSFeatures; }

    public boolean hasErrors() {
        return this.RAWSSummary != null && this.RAWSSummary.getResponseCode() != null && !this.RAWSSummary.getResponseCode().equals(RESPONSE_CODE_NO_ERRORS) && !this.RAWSSummary.getResponseCode().equals(RESPONSE_CODE_ZERO_RESULTS);
    }

    public String toString() {
        return String.format("[summary: %s, units: %s, type: %s]", RAWSSummary, RAWSUnits, type);
    }
}
