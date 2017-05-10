package edu.mit.ll.nics.processor.util;

import edu.mit.ll.nics.processor.raws.model.RAWSObservations;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.Date;

public class RAWSObservationsTest {

    private RAWSObservations rawsObservations;

    @Test
    public void testDescription() {
        rawsObservations = new RAWSObservations("ACTIVE", "SNVC", "SAMPLE NONE VAN CAR", "CA", 92.5, 78.0, 12.0,4,134,10,new Timestamp(new Date().getTime()), "http://test-site-with-more-observations");
        System.out.println(rawsObservations.getDescription());
    }
}
