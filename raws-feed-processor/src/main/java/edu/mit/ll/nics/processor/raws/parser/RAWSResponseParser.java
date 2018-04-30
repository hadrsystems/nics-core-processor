package edu.mit.ll.nics.processor.raws.parser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import edu.mit.ll.nics.processor.raws.model.RAWSResponse;

import java.io.IOException;

public class RAWSResponseParser {
    private ObjectMapper objectMapper = new ObjectMapper();

    public RAWSResponseParser() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
    }

    public RAWSResponse parse(String rawsGeoJson) throws IOException {
        return this.objectMapper.readValue(rawsGeoJson, RAWSResponse.class);
    }
}
