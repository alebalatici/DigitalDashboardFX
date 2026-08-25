package org.example.calculations;

import org.example.core.PointOfInterest;

public class Physics {
    /**
     * Earth's radius in km
     */
    public static final double R = 6371.0;

    private static final double SVG_WIDTH = 1009.6727;
    private static final double SVG_HEIGHT = 665.96301;

    //The map's geographical limits
    private static final double MIN_LON = -169.110266;
    private static final double MAX_LON = 190.486279;
    private static final double MAX_LAT = 83.600842;
    private static final double MIN_LAT = -58.508473;

    public static double HavesineDistance(double lat1, double lon1, double lat2, double lon2) {
        if (lat1 < -90.0 || lat1 > 90.0 || lat2 < -90.0 || lat2 > 90.0) {
            throw new IllegalArgumentException("Latitude must be between -90.0 and 90.0");
        }

        if (lon1 < -180 || lon1 > 180.0 || lon2 < -180.0 || lon2 > 180.0) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180.");
        }

        double latDifRadians = Math.toRadians((lat1 - lat2)/2);
        double lonDifRadians = Math.toRadians((lon1 - lon2)/2);

        double lat1Radians = Math.toRadians(lat1);
        double lat2Radians = Math.toRadians(lat2);

        double a = Math.sqrt(Math.pow(Math.sin(latDifRadians), 2) + Math.pow(Math.sin(lonDifRadians), 2) * Math.cos(lat1Radians) * Math.cos(lat2Radians));
        double c = 2 * Math.asin(Math.min(1.0, a));
        return R * c;
    }

    /**
     * Converts (x, y) to SVG format (x', y')
     * @param lat the lattitude of a point of interest
     * @param lon the longitude of a point of interest
     * @return an array that contains the two SVG coordinates
     */
    public static double[] convertToSvg(double lat, double lon) {
        double x = ((lon - MIN_LON) / (MAX_LON - MIN_LON)) * SVG_WIDTH;
        //double y = ((MAX_LAT - lat) / (MAX_LAT - MIN_LAT)) * SVG_HEIGHT;

        double latRad = Math.toRadians(lat);
        double minLatRad = Math.toRadians(MIN_LAT);
        double maxLatRad = Math.toRadians(MAX_LAT);

        double mercatorY = Math.log(Math.tan(Math.PI / 4.0 + latRad / 2.0));
        double mercatorMinY = Math.log(Math.tan(Math.PI / 4.0 + minLatRad / 2.0));
        double mercatorMaxY = Math.log(Math.tan(Math.PI / 4.0 + maxLatRad / 2.0));

        double y = SVG_HEIGHT * (1.0 - (mercatorY - mercatorMinY) / (mercatorMaxY - mercatorMinY)) + 1;
        return new double[]{x, y};
    }
}