package org.example.gui.components.simulation_view_components;

import org.example.core.PointOfInterest;

public class MapPoint {
    private final PointOfInterest pointOfInterest;
    private double x;
    private double y;

    MapPoint(PointOfInterest pointOfInterest, double x, double y) {
        this.pointOfInterest = pointOfInterest;
        this.x = x;
        this.y = y;
    }

    public PointOfInterest getPointOfInterest() {
        return pointOfInterest;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
