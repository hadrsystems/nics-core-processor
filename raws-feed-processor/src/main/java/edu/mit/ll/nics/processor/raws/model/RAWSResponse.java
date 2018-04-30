/**
 * Copyright (c) 2016-2016, Taborda Solutions
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.mit.ll.nics.processor.raws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RAWSResponse {

    protected static final String RESPONSE_CODE_NO_ERRORS = "1";
    protected static final String RESPONSE_CODE_ZERO_RESULTS = "2";
    protected static final String RESPONSE_CODE_AUTHENTICATION_ERROR = "200";
    protected static final String RESPONSE_CODE_VALIDATION_ERROR = "-1";

    @JsonProperty("SUMMARY")
    private RAWSSummary RAWSSummary;
    @JsonProperty("QC_SUMMARY")
    private RAWSQCSummary RAWSQCSummary;
    @JsonProperty("UNITS")
    private RAWSUnits RAWSUnits;
    private String type;
    @JsonProperty("features")
    private List<RAWSFeature> RAWSFeatures;

    public RAWSResponse() {
    }

    protected RAWSResponse(RAWSSummary RAWSSummary, RAWSQCSummary RAWSQCSummary, RAWSUnits RAWSUnits, String type, List<RAWSFeature> RAWSFeatures) {
        this.RAWSSummary = RAWSSummary;
        this.RAWSQCSummary = RAWSQCSummary;
        this.RAWSUnits = RAWSUnits;
        this.type = type;
        this.RAWSFeatures = RAWSFeatures;
    }

    public RAWSSummary getRAWSSummary() {
        return RAWSSummary;
    }

    public RAWSQCSummary getRAWSQCSummary() {return RAWSQCSummary;
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
