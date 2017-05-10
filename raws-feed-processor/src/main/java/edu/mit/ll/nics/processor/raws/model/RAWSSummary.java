package edu.mit.ll.nics.processor.raws.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class RAWSSummary {

    @JsonProperty("NUMBER_OF_OBJECTS")
    private int numberOfObjects;
    @JsonProperty("RESPONSE_CODE")
    private String responseCode;
    @JsonProperty("RESPONSE_MESSAGE")
    private String responseMessage;
    @JsonProperty("RESPONSE_TIME")
    private double responseTime;

    public RAWSSummary() {
    }

    public RAWSSummary(int numberOfObjects, String responseCode, String responseMessage, double responseTime) {
        this.numberOfObjects = numberOfObjects;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.responseTime = responseTime;
    }

    public int getNumberOfObjects() {
        return numberOfObjects;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public double getResponseTime() {
        return responseTime;
    }

    @Override
    public String toString() {
        return String.format("[numberOfObjects: %s, responseCode: %s, responseMessage: %s, responseTime: %s]", numberOfObjects, responseCode, responseMessage, responseTime);
    }
}
