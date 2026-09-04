package org.example.calculations;

import org.example.core.Hotel;
import org.example.core.PointOfInterest;
import org.example.core.RestStation;
import org.example.core.Restaurant;
import org.example.session.AppSessionTelemetryPreferences;

import java.time.LocalDateTime;

public class Edge {
    private final PointOfInterest source;
    private final PointOfInterest destination;
    private final double distanceKm;

    public Edge(PointOfInterest source, PointOfInterest destination) {
        this.source = source;
        this.destination = destination;
        this.distanceKm = CalculationsService.HavesineDistance(source, destination);
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

    public double getSpeedKmh(LocalDateTime arrivalTime) {
        return CalculationsService.SpeedKmh(source, destination, arrivalTime);
    }

    public double getDrivingTime(LocalDateTime arrivalTime) {
        return distanceKm / getSpeedKmh(arrivalTime);
    }

    public double getRoadAndStopDuration(LocalDateTime arrivalTime) {
        double stopMinutes = getDrivingTime(arrivalTime) * 60;
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

    public double getWeight(LocalDateTime arrivalTime) {
        RoutingCriterion routingCriterion = AppSessionTelemetryPreferences.getInstance().getRoutingCriterion();
        double weight = 0;
        switch (routingCriterion) {
            case MIN_DISTANCE -> weight = distanceKm;
            case MIN_TIME -> weight = getDrivingTime(arrivalTime);
            case BEST_SERVICES -> weight = 2.0 - getQualityScore();
        }
        return weight;
    }
}