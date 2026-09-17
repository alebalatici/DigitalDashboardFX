package org.example.algorithms;

import org.example.core.PointOfInterest;

public class JourneyState implements Comparable<JourneyState> {
    public final PointOfInterest node;
    public final double cost;
    public final double driveHoursSinceRest;
    public final double driveHoursToday;
    public final double fuelLiters;
    public final boolean hasStopped;

    JourneyState(PointOfInterest node, double cost, double driveHoursSinceRest, double driveHoursToday, double fuelLiters, boolean hasStopped) {
        this.node = node;
        this.cost = cost;
        this.driveHoursSinceRest = driveHoursSinceRest;
        this.driveHoursToday = driveHoursToday;
        this.fuelLiters = fuelLiters;
        this.hasStopped = hasStopped;
    }

    public JourneyState(PointOfInterest node, double cost, double driveHoursSinceRest, double driveHoursToday, double fuelLiters) {
        this(node, cost, driveHoursSinceRest, driveHoursToday, fuelLiters, false);
    }

    @Override
    public int compareTo(JourneyState other) {
        return Double.compare(this.cost, other.cost);
    }
}