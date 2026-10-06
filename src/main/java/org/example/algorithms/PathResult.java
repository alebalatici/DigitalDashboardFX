package org.example.algorithms;

import org.example.calculations.Edge;
import org.example.core.PointOfInterest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PathResult {
    private final List<Edge> path;

    private final double totalCost;

    private final double totalHours;
    private final double totalKm;

    private final Map<PointOfInterest, LocalDateTime> arrivalTimes;

    public PathResult(List<Edge> path, double totalCost, double totalHours, double totalKm, Map<PointOfInterest, LocalDateTime> arrivalTimes) {
        this.path = path;
        this.totalCost = totalCost;
        this.totalHours = totalHours;
        this.totalKm = totalKm;
        this.arrivalTimes = arrivalTimes;
    }

    public List<Edge> getPath() {
        return path;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public double getTotalKm() {
        return totalKm;
    }

    public Map<PointOfInterest, LocalDateTime> getArrivalTimes() {
        return arrivalTimes;
    }

    public List<PointOfInterest> getPointsOfInterest() {
        List<PointOfInterest> points = new ArrayList<>();
        if (!path.isEmpty()) {
            points.add(path.get(0).getSource());
            for (Edge edge : path) {
                points.add(edge.getDestination());
            }
        }
        return points;
    }
}