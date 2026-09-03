package org.example.calculations;

import org.example.core.Hotel;
import org.example.core.PointOfInterest;
import org.example.core.RestStation;
import org.example.core.Restaurant;

import java.time.LocalDateTime;

public class Edge {
    private final PointOfInterest source;
    private final PointOfInterest destination;
    private final double speedKmh;
    private final double distanceKm;


    public Edge(PointOfInterest source, PointOfInterest destination, LocalDateTime arrivalTime) {
        this.source = source;
        this.destination = destination;
        this.distanceKm = CalculationsService.HavesineDistance(source, destination);
        this.speedKmh = CalculationsService.SpeedKmh(source, destination, arrivalTime);
    }

    public PointOfInterest getSource() {
        return source;
    }

    public PointOfInterest getDestination() {
        return destination;
    }

    public double getSpeedKmh() {
        return speedKmh;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public double getDrivingTime() {
        return distanceKm / speedKmh;
    }

    public double getRoadAndStopDuration() {
        double stopMinutes = getDrivingTime() * 60;
        if (destination instanceof RestStation) {
            stopMinutes += ((RestStation) destination).getAverageStopDuration();
        }
        return stopMinutes / 60;
    }

    public double getQualityScore() {
        double qualityScore = 0;
        if (destination instanceof RestStation) {
            if (destination instanceof Hotel) {
                qualityScore = (double) ((Hotel) destination).getStars() / 5;
            }
            if (destination instanceof Restaurant) {
                qualityScore = ((Restaurant) destination).getRating() / 10.0;
            }
            else {
                  qualityScore = 1.0;
            }
        }
        return qualityScore;
    }

    public double getWeight() {
        RoutingCriterion routingCriterion = AppSessionCALC.getInstance().getRoutingCriterion();
        double weight = 0;
        switch (routingCriterion) {
            case MIN_DISTANCE -> weight = distanceKm;
            case MIN_TIME -> weight = getDrivingTime();
            case BEST_SERVICES -> weight = 2.0 - getQualityScore();
        }
        return weight;
    }
}