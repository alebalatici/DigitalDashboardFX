package org.example.gui.components.simulation_view_components;

import org.example.core.PointOfInterest;

import java.util.List;

public class BoundingBox {
    private double south = Double.MAX_VALUE, north = -Double.MAX_VALUE;
    private double west = Double.MAX_VALUE, east = -Double.MAX_VALUE;

    private BoundingBox() {

    }

    public static BoundingBox from(List<PointOfInterest> points) {
        BoundingBox box = new BoundingBox();
        for (PointOfInterest point : points) {
            double lat = point.getX();
            double lon = point.getY();

            if (lat < box.south) { box.south = lat; }
            if (lat > box.north) { box.north = lat; }
            if (lon < box.west) { box.west = lon; }
            if (lon > box.east) { box.east = lon; }
        }
        return box;
    }

    public double getNormalizedX(double lon) {
        double delta = east - west;
        if (Math.abs(delta) < 0.00001) {
            return 0.5;
        }
        return (lon - west) / delta;
    }

    public double getNormalizedY(double lat) {
        double delta = north - south;
        if (Math.abs(delta) < 0.00001) {
            return 0.5;
        }
        return (lat - south) / delta;
    }

    public double getSouth() {
        return south;
    }

    public double getNorth() {
        return north;
    }

    public double getWest() {
        return west;
    }

    public double getEast() {
        return east;
    }
}