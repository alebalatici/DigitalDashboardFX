package org.example.calculations;

import org.example.core.PointOfInterest;

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

        public PathResult(List<Edge> path, double totalCost, double totalMinutes, double totalKm) {
            this.path = path;
            this.totalCost = totalCost;
            this.totalMinutes = totalMinutes;
            this.totalKm = totalKm;
        }

        public List<Edge> getPath() {
            return path;
        }

        public double getTotalCost() {
            return totalCost;
        }
    }

    public static PathResult dijkstra(
            Map<PointOfInterest, List<Edge>> adjacencyList,
            PointOfInterest start,
            PointOfInterest destination) {
        Map<PointOfInterest, Double> minCosts = new HashMap<>();
        Map<PointOfInterest, Edge> edgeTo = new HashMap<>();

        PriorityQueue<NodeWrapper> priorityQueue = new PriorityQueue<>();

        minCosts.put(start, 0.0);
        priorityQueue.add(new NodeWrapper(start, 0.0));

        double totalMinutes = 0.0;
        double totalKm = 0.0;

        while (!priorityQueue.isEmpty()) {
            NodeWrapper currentWrapper = priorityQueue.poll();
            PointOfInterest current = currentWrapper.node;

            if (current.equals(destination)) {
                break;
            }

            if (currentWrapper.cost > minCosts.getOrDefault(current, Double.MAX_VALUE)) {
                continue;
            }

            List<Edge> neighbours = adjacencyList.getOrDefault(current, Collections.emptyList());
            for (Edge edge : neighbours) {
                PointOfInterest neighbour = edge.getDestination();
                double edgeWeight = edge.getWeight();
                double newCost = minCosts.get(current) + edgeWeight;

                if (newCost < minCosts.getOrDefault(neighbour, Double.MAX_VALUE)) {
                    minCosts.put(neighbour, newCost);
                    edgeTo.put(neighbour, edge);
                    priorityQueue.add(new NodeWrapper(neighbour, newCost));
                }
            }
        }

        if (!minCosts.containsKey(destination)) {
            return null;
        }

        List<Edge> path = new ArrayList<>();
        PointOfInterest current = destination;

        while (!current.equals(start)) {
            Edge edge = edgeTo.get(current);
            if (edge == null) break;
            path.add(edge);

            totalKm += edge.getDistanceKm();
            totalMinutes += edge.getRoadAndStopDuration();

            current = edge.getSource();
        }

        Collections.reverse(path);
        return new PathResult(path, minCosts.get(destination), totalMinutes, totalKm);
    }
}