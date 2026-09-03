package org.example.calculations;
import org.example.core.City;
import org.example.core.PointOfInterest;
import org.example.core.VirtualPoint;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Random;

public class CalculationsService {
    private static final Random random = new Random();

    /**
     * Calculates the Havesine Distance between 2 points of interest
     * @param p1 The first point of interest
     * @param p2 The second point of interest
     * @return The Havesine distance between the 2 points of interest
     */
    public static double HavesineDistance(PointOfInterest p1, PointOfInterest p2) {
        try {
            return Physics.HavesineDistance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        }
        catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * Generates a random traffic factor
     * 0.90 -> no traffic
     * 1.60 -> hard traffic
     * @return The random traffic factor
     */
    public static double generateTrafficFactor() {
        return 0.90 + (1.60 - 0.90) * random.nextDouble();
    }

    /**
     * Returns the default speed for a journey depending on the speed preference
     * @return the default speed for a journey depending on the speed preference
     */
    private static double DefaultSpeed() {
        DistancePreference preference = AppSessionCALC.getInstance().getDistancePreference();
        double defaultSpeed = 0;

        switch (preference) {
            case SHORT -> {
                defaultSpeed = 60 + (90 - 60) * random.nextDouble();
            }
            case MEDIUM -> {
                defaultSpeed = 80 + (110 - 80) * random.nextDouble();
            }
            case LONG -> {
                defaultSpeed = 100 + (130 - 100) * random.nextDouble();
            }
            case CUSTOM -> {
                double minimumSpeed = AppSessionCALC.getInstance().getCustomMinSpeed();
                double maximumSpeed = AppSessionCALC.getInstance().getCustomMaxSpeed();
                defaultSpeed = minimumSpeed + (maximumSpeed - minimumSpeed) * random.nextDouble();
            }
        }
        return defaultSpeed;
    }

    /**
     * Returns the congestion factor of the city in a given day
     * @param city The city
     * @param arrivalTime The arrival time
     * @return The city's congestion factor
     */
    public static double getCityCongestionFactor(City city, LocalDateTime arrivalTime) {
        DayOfWeek day = arrivalTime.getDayOfWeek();
        boolean isWeekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);

        if (isWeekend) {
            return city.getWeekendCongestionFactor();
        }

        return city.getWeekdayCongestionFactor();
    }

    /**
     * Returns the speed in Km/h that the vehicle will have between p1 and p2
     * We assume that the vehicle's speed is uniform throughout the journey between p1 and p2
     * @param p1 The first point of interest
     * @param p2 The second point of interest
     * @param arrivalTime The arrival time
     * @return The vehicle's speed
     */
    public static double SpeedKmh(PointOfInterest p1, PointOfInterest p2, LocalDateTime arrivalTime) {
        double openRoadSpeed = DefaultSpeed() / generateTrafficFactor();

        boolean isP1City = p1 instanceof City;
        boolean isP2City = p2 instanceof City;

        if (!isP1City && !isP2City) {
            return openRoadSpeed;
        }

        City city;
        if (isP1City) {
            city = (City) p1;
        }

        else {
            city = (City) p2;
        }

        return openRoadSpeed / getCityCongestionFactor(city, arrivalTime);
    }

    /**
     * Creates a new virtual point at a given distance from p1
     * @param p1 The first point of interest
     * @param p2 The second point of interest
     * @param distanceFromP1 The distance from p1 where the point is created
     * @return The new virtual point
     */
    public static VirtualPoint createVirtualPointAtDistance(PointOfInterest p1, PointOfInterest p2, double distanceFromP1) {
        try {
            double[] coordinates = Physics.virtualPointCoordinates(p1.getX(), p1.getY(), p2.getX(), p2.getY(), distanceFromP1);
            return new VirtualPoint(p1.getName(), coordinates[0], coordinates[1]);
        }

        catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }
}