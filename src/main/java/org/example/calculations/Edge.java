package org.example.calculations;

import org.example.core.City;
import org.example.core.PointOfInterest;
import org.example.core.Vehicle;

public class Edge {
    private final PointOfInterest source;
    private final PointOfInterest destination;

    //private final double distanceKm;

    public Edge(PointOfInterest source, PointOfInterest destination) {
        this.source = source;
        this.destination = destination;
    }

    public PointOfInterest getSource() {
        return source;
    }

    public PointOfInterest getDestination() {
        return destination;
    }
}
