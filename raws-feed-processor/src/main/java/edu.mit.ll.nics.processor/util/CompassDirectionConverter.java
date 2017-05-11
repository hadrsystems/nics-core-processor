package edu.mit.ll.nics.processor.util;

public class CompassDirectionConverter {
    //                                                  0, 22.5,   45,   67.5 90, 112.5, 135,157.5,180,202.5,225, 247.5,270,292.5,315,337.5,360
    private static final String[] COMPASS_DIRECTIONS = {"N","NNE", "NE","ENE","E","ESE","SE","SSE","S","SSW","SW","WSW","W","WNW","NW","NNW","N"};
    private static final double DEGREES_PER_COMPASS_DIRECTION = 22.5;
    private static final int TOTAL_DEGREES = 360;

    public String getCompassDirection(double directionInDegrees) {
        double normalizedDirectionInDegrees = directionInDegrees % TOTAL_DEGREES;
        long compassDirectionIndex = Math.round(normalizedDirectionInDegrees/DEGREES_PER_COMPASS_DIRECTION);
        return COMPASS_DIRECTIONS[(int) compassDirectionIndex];
    }
}
