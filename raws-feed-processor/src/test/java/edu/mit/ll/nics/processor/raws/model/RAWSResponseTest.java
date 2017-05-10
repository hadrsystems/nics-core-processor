package edu.mit.ll.nics.processor.raws.model;

import junit.framework.Assert;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.testng.annotations.Test;

public class RAWSResponseTest {

    RAWSResponse rawsResponse;

    @Test
    public void verifyNoErrors() {
        RAWSSummary RAWSSummary = new RAWSSummary(1, RAWSResponse.RESPONSE_CODE_NO_ERRORS, "Not an Error message", 0.0);

        RAWSResponse rawsResponse = new RAWSResponse(RAWSSummary, null, "FeatureCollection", null);
        Assert.assertFalse(rawsResponse.hasErrors());
    }

    @Test
    public void verifyNoErrorsWhenSummaryHasZeroResults() {
        RAWSSummary RAWSSummary = new RAWSSummary(1, RAWSResponse.RESPONSE_CODE_ZERO_RESULTS, "Not an Error message", 0.0);

        RAWSResponse rawsResponse = new RAWSResponse(RAWSSummary, null, "FeatureCollection", null);
        Assert.assertFalse(rawsResponse.hasErrors());
    }

    @Test
    public void verifyNoErrorsWhenResponseCodeHasAuthenticationErrors() {
        RAWSSummary RAWSSummary = new RAWSSummary(1, RAWSResponse.RESPONSE_CODE_AUTHENTICATION_ERROR, "Authentication Error message", 0.0);

        RAWSResponse rawsResponse = new RAWSResponse(RAWSSummary, null, "FeatureCollection", null);
        Assert.assertTrue(rawsResponse.hasErrors());
    }

    @Test
    public void verifyNoErrorsWhenRequestHasInvalidParameters() {
        RAWSSummary RAWSSummary = new RAWSSummary(1, RAWSResponse.RESPONSE_CODE_VALIDATION_ERROR, "Validation Error message", 0.0);

        RAWSResponse rawsResponse = new RAWSResponse(RAWSSummary, null, "FeatureCollection", null);
        Assert.assertTrue(rawsResponse.hasErrors());
    }

    @Test
    public void verifyNoErrorsWhenRequestReturnsZeroResults() {
        RAWSSummary RAWSSummary = new RAWSSummary();

        RAWSResponse rawsResponse = new RAWSResponse(RAWSSummary, null, "FeatureCollection", null);
        Assert.assertFalse(rawsResponse.hasErrors());
    }
}
