package org.example.algorithms;

import org.example.calculations.CalculationsService;
import org.example.calculations.Edge;
import org.example.calculations.ServiceException;
import org.example.core.*;
import org.example.session.AppSessionTelemetryPreferences;

import java.time.LocalDateTime;
import java.util.*;

public class Dijkstra {
    public static JourneyState applyNodeRefuelAndRestRules(JourneyState currentState, Vehicle vehicle) {
        PointOfInterest current = currentState.node;
        double fuel = currentState.fuelLiters;
        double restHours = currentState.driveHoursSinceRest;
        double todayHours = currentState.driveHoursToday;

        double maxDrivingHours = AppSessionTelemetryPreferences.getInstance().getMaxDriveHoursBeforeRest();
        double maxHoursBeforeHotel = AppSessionTelemetryPreferences.getInstance().getMaxDriveHoursBeforeHotel();

        double differenceRest = Math.abs(restHours - maxDrivingHours);
        double differenceHotel = Math.abs(todayHours - maxHoursBeforeHotel);

        boolean hasStopped = false;

        if (current instanceof GasStation) {
            double minFuelThreshold = vehicle.getFuelCapacity() * 0.20;
            if (currentState.fuelLiters <= minFuelThreshold || differenceRest <= 1) {
                fuel = vehicle.getFuelCapacity();
                restHours = 0.0;
                hasStopped = true;
            }
        }

        if (current instanceof Restaurant && differenceRest <= 1) {
            restHours = 0.0;
            hasStopped = true;
        }

        if (current instanceof Hotel && differenceHotel <= 1) {
            restHours = 0.0;
            todayHours = 0.0;
            hasStopped = true;
        }

        return new JourneyState(current, currentState.cost, restHours, todayHours, fuel, hasStopped);
    }

    public static boolean isValidEdgeTransition(JourneyState currentState, PointOfInterest neighbour, PointOfInterest destination, double driveDurationHours, double fuelNeeded) {
        boolean reachesDestination = neighbour.equals(destination);
        double fuelAfterDrive = currentState.fuelLiters - fuelNeeded;

        if (fuelAfterDrive < 0) {
            return false;
        }

        /*
        if (fuelAfterDrive <= 15 && !(neighbour instanceof GasStation) && !reachesDestination) {
            return false;
        }*/

        double maxHoursBeforeHotel = AppSessionTelemetryPreferences.getInstance().getMaxDriveHoursBeforeHotel();
        boolean reachesHotel = neighbour instanceof Hotel;
        double totalHoursToday = currentState.driveHoursToday + driveDurationHours;

        if (maxHoursBeforeHotel - totalHoursToday < 0 && !reachesHotel && !reachesDestination) {
            return false;
        }

        return true;
    }

    static boolean validNeighbours(PointOfInterest current, PointOfInterest neighbour) {
        if (current instanceof City && !(neighbour instanceof VirtualPoint)) return false;
        if (neighbour instanceof City && !(current instanceof VirtualPoint)) return false;
        return true;
    }

    public static PathResult reconstructPath(PointOfInterest start,
                                             PointOfInterest destination,
                                             Map<PointOfInterest, Edge> parent,
                                             Map<PointOfInterest, Double> minCosts,
                                             Map<PointOfInterest, LocalDateTime> arrivalTimes) {
        List<Edge> path = new ArrayList<>();
        PointOfInterest current = destination;

        double totalMinutes = 0.0;
        double totalKm = 0.0;

        while (!current.equals(start)) {
            Edge edge = parent.get(current);
            if (edge == null) {
                break;
            }

            path.add(edge);

            totalKm += edge.getDistanceKm();
            LocalDateTime sourceArrival = arrivalTimes.get(edge.getSource());
            LocalDateTime destinationArrival = arrivalTimes.get(edge.getDestination());
            if (sourceArrival != null && destinationArrival != null) {
                totalMinutes += java.time.Duration.between(sourceArrival, destinationArrival).toMinutes();
            }
            current = edge.getSource();
        }

        Collections.reverse(path);
        return new PathResult(path, minCosts.get(destination), totalMinutes, totalKm, arrivalTimes);
    }

    public static double penalizeEdgeWeight(Vehicle vehicle, JourneyState currentState, PointOfInterest neighbour, PointOfInterest destination) {
        double fuelRatio = currentState.fuelLiters / vehicle.getFuelCapacity();
        double totalPenalization = 0.0;
        if (fuelRatio < 0.30 && !(neighbour instanceof GasStation) && !(neighbour.equals(destination))) {
            totalPenalization += 5000.0 * (0.30 - fuelRatio);
        }

        double maxHoursSinceRest = AppSessionTelemetryPreferences.getInstance().getMaxDriveHoursBeforeRest();
        double hoursSinceRest = currentState.driveHoursSinceRest;
        double hoursSinceRestRatio = hoursSinceRest / maxHoursSinceRest;

        if (hoursSinceRestRatio > 0.80 && !(neighbour instanceof Restaurant) && !(neighbour instanceof GasStation) && !(neighbour.equals(destination))) {
            totalPenalization += 500.0 * (hoursSinceRestRatio - 0.70);
        }

        double maxHoursSinceHotel = AppSessionTelemetryPreferences.getInstance().getMaxDriveHoursBeforeHotel();
        double hoursSinceHotel = currentState.driveHoursToday;
        double hoursSinceHotelRatio = hoursSinceHotel / maxHoursSinceHotel;
        if (hoursSinceHotelRatio > 0.80 && !(neighbour instanceof Hotel) && !(neighbour.equals(destination))) {
            totalPenalization += 500.0 * (hoursSinceHotelRatio - 0.70);
        }

        return totalPenalization;
    }

    public static PathResult dijkstra(
            Map<PointOfInterest, List<Edge>> adjacencyList,
            PointOfInterest start,
            PointOfInterest destination,
            LocalDateTime startDateTime,
            Vehicle vehicle) {

        if (adjacencyList == null || start == null || destination == null || startDateTime == null || vehicle == null) {
            throw new ServiceException("Null arguments provided to Dijkstra algorithm");
        }

        //The array of the minimum costs to each location (Point of Interest)
        Map<PointOfInterest, Double> minCosts = new HashMap<>();

        //The array of parent edges that connects a Point of Interest to the parent edge
        Map<PointOfInterest, Edge> parent = new HashMap<>();

        //The arrival times to each location (Point of Interest)
        Map<PointOfInterest, LocalDateTime> arrivalTimes = new HashMap<>();

        PriorityQueue<JourneyState> priorityQueue = new PriorityQueue<>();

        minCosts.put(start, 0.0);
        arrivalTimes.put(start, startDateTime);
        priorityQueue.add(new JourneyState(start, 0.0, 0.0, 0.0, vehicle.getCurrentFuel()));

        while (!priorityQueue.isEmpty()) {
            JourneyState currentState = priorityQueue.poll();
            PointOfInterest current = currentState.node;

            if (current.equals(destination)) {
                break;
            }

            if (currentState.cost > minCosts.getOrDefault(current, Double.MAX_VALUE)) {
                continue;
            }

            LocalDateTime currentArrival = arrivalTimes.get(current);

            JourneyState updatedState = applyNodeRefuelAndRestRules(currentState, vehicle);
            List<Edge> neighbours = adjacencyList.getOrDefault(current, Collections.emptyList());

            for (Edge edge : neighbours) {
                PointOfInterest neighbour = edge.getDestination();

                if (!validNeighbours(updatedState.node, neighbour)) continue;


                double driveDurationHours;
                double fuelNeeded;
                double edgeWeight;
                if (current instanceof City || neighbour instanceof City) {
                    double trafficFactor;
                    if (current instanceof City) {
                        trafficFactor = CalculationsService.getCityCongestionFactor((City) current, currentArrival);
                    }

                    else {
                        trafficFactor = CalculationsService.getCityCongestionFactor((City) neighbour, currentArrival);
                    }
                    driveDurationHours = edge.getDrivingTime(trafficFactor);
                    fuelNeeded = edge.getFuelConsumption(vehicle, trafficFactor);
                    edgeWeight = edge.getWeight(trafficFactor);
                }

                else {
                    driveDurationHours = edge.getDrivingTime();
                    fuelNeeded = edge.getFuelConsumption(vehicle);
                    edgeWeight = edge.getWeight();
                }

                edgeWeight += penalizeEdgeWeight(vehicle, updatedState, neighbour, destination);

                if (!isValidEdgeTransition(updatedState, neighbour, destination, driveDurationHours, fuelNeeded)) {
                    continue;
                }

                double newCost = minCosts.get(current) + edgeWeight;
                if (newCost < minCosts.getOrDefault(neighbour, Double.MAX_VALUE)) {
                    minCosts.put(neighbour, newCost);
                    parent.put(neighbour, edge);

                    long durationMinutes = (long) (driveDurationHours * 60);
                    LocalDateTime nextArrival = currentArrival.plusMinutes(durationMinutes);

                    if (updatedState.node instanceof RestStation && updatedState.hasStopped) {
                        assert current instanceof RestStation;
                        nextArrival = nextArrival.plusMinutes(((RestStation) current).getAverageStopDuration());
                    }

                    arrivalTimes.put(neighbour, nextArrival);
                    priorityQueue.add(new JourneyState(neighbour, newCost, updatedState.driveHoursSinceRest + driveDurationHours, updatedState.driveHoursToday + driveDurationHours, updatedState.fuelLiters - fuelNeeded));
                }
            }
        }

        if (!minCosts.containsKey(destination)) {
            return null;
        }

        return reconstructPath(start, destination, parent, minCosts, arrivalTimes);
    }
}