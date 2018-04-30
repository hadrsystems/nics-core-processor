package edu.mit.ll.nics.processor.raws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RAWSQCSummary {

    @JsonProperty("QC_SHORTNAMES")
    private HashMap<Integer,String> shortNames;
    @JsonProperty("QC_NAMES")
    private HashMap<Integer,String> names;

    public RAWSQCSummary() {

    }

    public RAWSQCSummary(HashMap<Integer,String> ShortNames, HashMap<Integer,String> Names) {
        this.shortNames = ShortNames;
        this.names = Names;
    }

    public HashMap<Integer, String> getShortNames() {
        return shortNames;
    }

    public HashMap<Integer, String> getNames() {
        return names;
    }
}
