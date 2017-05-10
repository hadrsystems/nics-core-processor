package edu.mit.ll.nics.processor.raws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RAWSFeature {

    @JsonProperty("geometry")
    private RAWSFeatureGeometry rawsFeatureGeometry;
    private String type;
    @JsonProperty("properties")
    private RAWSObservations rawsObservations;

    public RAWSFeature() {
    }

    public RAWSFeature(RAWSFeatureGeometry rawsFeatureGeometry, String type, RAWSObservations rawsObservations) {
        this.rawsFeatureGeometry = rawsFeatureGeometry;
        this.type = type;
        this.rawsObservations = rawsObservations;
    }

    public RAWSFeatureGeometry getRawsFeatureGeometry() {
        return rawsFeatureGeometry;
    }

    public String getType() { return type;}

    public RAWSObservations getRawsObservations() {
        return rawsObservations;
    }

    public String toString() {
        return String.format("[featureGeometry: %s, type: %s, featureObservations: %s]", rawsFeatureGeometry, type, rawsObservations);
    }
}
