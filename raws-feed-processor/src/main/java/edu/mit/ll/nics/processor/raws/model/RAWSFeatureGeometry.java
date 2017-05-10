package edu.mit.ll.nics.processor.raws.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class RAWSFeatureGeometry {

    private String type;
    private List<Double> coordinates = new ArrayList<Double>();

    public RAWSFeatureGeometry() {}

    public RAWSFeatureGeometry(String type, List<Double> coordinates) {
        this.type = type;
        this.coordinates.addAll(coordinates);
    }

    public String getType() {
        return type;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public String toString() {
        return String.format("type: %s, coordinates: %s]", type, coordinates);
    }
}
