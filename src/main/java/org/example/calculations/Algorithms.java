package org.example.calculations;

import org.example.core.PointOfInterest;

import java.time.LocalDateTime;
import java.util.*;

public class Algorithms {
    private static class NodeWrapper implements Comparable<NodeWrapper> {
        final PointOfInterest node;
        final double cost;

        NodeWrapper(PointOfInterest node, double cost) {
            this.node = node;
            this.cost = cost;
        }

        @Override
        public int compareTo(NodeWrapper o) {
            return Double.compare(cost, o.cost);
        }
    }

    public static class PathResult {
        private final List<Edge> path;
        private final double totalCost;

        private final double totalMinutes;
        private final double totalKm;

        private final Map<PointOfInterest, LocalDateTime> arrivalTimes;

        public PathResult(List<Edge> path, double totalCost, double totalMinutes, double totalKm, Map<PointOfInterest, LocalDateTime> arrivalTimes) {
            this.path = path;
            this.totalCost = totalCost;
            this.totalMinutes = totalMinutes;
            this.totalKm = totalKm;
            this.arrivalTimes = arrivalTimes;
        }

        public List<Edge> getPath() {
            return path;
        }

        public double getTotalCost() {
            return totalCost;
        }

        public double getTotalMinutes() {
            return totalMinutes;
        }

        public double getTotalKm() {
            return totalKm;
        }

        public Map<PointOfInterest, LocalDateTime> getArrivalTimes() {
            return arrivalTimes;
        }
    }

    public static PathResult dijkstra(
            Map<PointOfInterest, List<Edge>> adjacencyList,
            PointOfInterest start,
            PointOfInterest destination,
            LocalDateTime startDateTime) {

        if (adjacencyList == null || start == null || destination == null || startDateTime == null) {
            throw new ServiceException("Null arguments provided to Dijkstra algorithm");
        }

        //The array of the minimum costs to each location (Point of Interest)
        Map<PointOfInterest, Double> minCosts = new HashMap<>();

        //The array of parent edges that connects a Point of Interest to the parent edge
        Map<PointOfInterest, Edge> parent = new HashMap<>();

        //The arrival times to each location (Point of Interest)
        Map<PointOfInterest, LocalDateTime> arrivalTimes = new HashMap<>();

        PriorityQueue<NodeWrapper> priorityQueue = new PriorityQueue<>();

        minCosts.put(start, 0.0);
        arrivalTimes.put(start, startDateTime);
        priorityQueue.add(new NodeWrapper(start, 0.0));

        while (!priorityQueue.isEmpty()) {
            NodeWrapper currentWrapper = priorityQueue.poll();
            PointOfInterest current = currentWrapper.node;

            if (current.equals(destination)) {
                break;
            }

            if (currentWrapper.cost > minCosts.getOrDefault(current, Double.MAX_VALUE)) {
                continue;
            }

            LocalDateTime currentArrival = arrivalTimes.get(current);

            List<Edge> neighbours = adjacencyList.getOrDefault(current, Collections.emptyList());
            for (Edge edge : neighbours) {
                PointOfInterest neighbour = edge.getDestination();
                double edgeWeight = edge.getWeight(currentArrival);
                double newCost = minCosts.get(current) + edgeWeight;

                if (newCost < minCosts.getOrDefault(neighbour, Double.MAX_VALUE)) {
                    minCosts.put(neighbour, newCost);
                    parent.put(neighbour, edge);

                    double durationHours = edge.getRoadAndStopDuration(currentArrival);
                    long durationMinutes = (long) (durationHours * 60);

                    LocalDateTime nextArrival = currentArrival.plusMinutes(durationMinutes);
                    arrivalTimes.put(neighbour, nextArrival);
                    priorityQueue.add(new NodeWrapper(neighbour, newCost));
                }
            }
        }

        if (!minCosts.containsKey(destination)) {
            return null;
        }

        List<Edge> path = new ArrayList<>();
        PointOfInterest current = destination;

        double totalMinutes = 0.0;
        double totalKm = 0.0;

        while (!current.equals(start)) {
            Edge edge = parent.get(current);
            if (edge == null) break;
            path.add(edge);

            totalKm += edge.getDistanceKm();

            LocalDateTime sourceArrival = arrivalTimes.get(edge.getSource());
            totalMinutes += edge.getRoadAndStopDuration(sourceArrival) * 60;

            current = edge.getSource();
        }

        Collections.reverse(path);
        return new PathResult(path, minCosts.get(destination), totalMinutes, totalKm, arrivalTimes);
    }
}