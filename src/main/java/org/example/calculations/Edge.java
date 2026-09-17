package org.example.calculations;

import org.example.core.*;
import org.example.session.AppSessionTelemetryPreferences;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Edge {
    private final PointOfInterest source;
    private final PointOfInterest destination;
    private final double distanceKm;
    private final double trafficFactor;
    private final double defaultSpeed;

    public Edge(PointOfInterest source, PointOfInterest destination) {
        this.source = source;
        this.destination = destination;
        this.distanceKm = CalculationsService.HavesineDistance(source, destination);
        this.trafficFactor = CalculationsService.generateTrafficFactor();
        this.defaultSpeed = CalculationsService.initializeDefaultSpeed();
    }

    public PointOfInterest getSource() {
        return source;
    }

    public PointOfInterest getDestination() {
        return destination;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public double getSpeedKmh() {
        return getSpeedKmh(1);
    }

    public double getSpeedKmhWithTrafficCalculator(City city, LocalDateTime arrivalTime) {
        return getSpeedKmh(CalculationsService.getCityCongestionFactor(city, arrivalTime));
    }

    public double getSpeedKmh(double cityTrafficFactor) {
        return CalculationsService.SpeedKmh(source, destination, this.trafficFactor, this.defaultSpeed) / cityTrafficFactor;
    }

    public double getDrivingTime() {
        return getDrivingTime(1);
    }

    public double getDrivingTime(double cityTrafficFactor) {
        return distanceKm / getSpeedKmh(cityTrafficFactor);
    }

    /**
     * Returns the duration of the drive + the duration of the stop at the given location in hours
     * @return the total minutes being spent driving + stoping at a certain location in hours
     */
    public double getRoadAndStopDuration() {
        double stopMinutes = getDrivingTime() * 60;
        if (destination instanceof RestStation) {
            stopMinutes += ((RestStation) destination).getAverageStopDuration();
        }
        return stopMinutes / 60;
    }

    public double getQualityScore() {
        if (destination instanceof Hotel) {
            return (double) ((Hotel) destination).getStars() / 5;
        }
        else if (destination instanceof Restaurant) {
            return ((Restaurant) destination).getRating() / 10.0;
        }
        return 1.0;
    }

    public double getWeight(double cityTrafficFactor) {
        RoutingCriterion routingCriterion = AppSessionTelemetryPreferences.getInstance().getRoutingCriterion();
        double weight = 0;
        switch (routingCriterion) {
            case MIN_DISTANCE -> weight = distanceKm;
            case MIN_TIME -> weight = getDrivingTime(cityTrafficFactor);
            case BEST_SERVICES -> weight = 2.0 - getQualityScore();
        }
        return weight;
    }

    public double getWeight() {
        return getWeight(1);
    }

    public double getFuelConsumption(Vehicle vehicle, double cityTrafficFactor) {
        double trafficFactor = this.trafficFactor * cityTrafficFactor;
        double baseConsumption = (this.distanceKm / 100) * vehicle.getBaseConsumption();
        return baseConsumption * trafficFactor;
    }

    public double getFuelConsumption(Vehicle vehicle) {
       /* double trafficFactor = this.trafficFactor;
        double baseConsumption = (this.distanceKm / 100) * vehicle.getBaseConsumption();
        return baseConsumption * trafficFactor;*/

        return getFuelConsumption(vehicle, 1);
    }
}