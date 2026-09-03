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

    /**
     * Calculatest the Havesine Distance between 2 points
     * @param lat1 the lattitude of the first point
     * @param lon1 the longitude of the first point
     * @param lat2 the lattitude of the second point
     * @param lon2 the longitude of the second point
     * @return The Havesine Distance
     */
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

    /**
     * Returns the coordinates of a point that has the distance of distanceInKm from the first point
     * @param p1X The x coordinate of the first point
     * @param p1Y The y coordinate of the first point
     * @param p2X The x coordinate of the second point
     * @param p2Y The y coordinate of the second point
     * @param distanceInKm The distance in km from the first point
     * @return The coordinates of a point that has the distance of fistanceInKm from the first point
     */
    public static double[] virtualPointCoordinates(double p1X, double p1Y, double p2X, double p2Y, double distanceInKm) {
        double totalDistance = HavesineDistance(p1X, p1Y, p2X, p2Y);

        if (totalDistance <= distanceInKm) {
            throw new IllegalArgumentException("Distance in km must not be greater than the total distance between p1 and p2.");
        }

        double lat1 = Math.toRadians(p1X);
        double lon1 = Math.toRadians(p1Y);
        double lat2 = Math.toRadians(p2X);
        double lon2 = Math.toRadians(p2Y);

        double delta = totalDistance / R;
        double f = distanceInKm / totalDistance;

        double a = Math.sin((1 - f) * delta) / Math.sin(delta);
        double b = Math.sin(f * delta) / Math.sin(delta);

        double x = a * Math.cos(lat1) * Math.cos(lon1) + b * Math.cos(lat2) * Math.cos(lon2);
        double y = a * Math.cos(lat1) * Math.sin(lon1) + b * Math.cos(lat2) * Math.sin(lon2);
        double z = a * Math.sin(lat1) + b * Math.sin(lat2);

        double latVirtualRad = Math.atan2(z, Math.sqrt(x * x + y * y));
        double lonVirtualRad = Math.atan2(y, x);

        double virtualX = Math.toDegrees(latVirtualRad);
        double virtualY = Math.toDegrees(lonVirtualRad);
        return new double[]{virtualX, virtualY};
    }
}