package org.example.gui.components.simulation_view_components;

import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import org.example.calculations.Edge;
import org.example.core.City;
import org.example.core.PointOfInterest;
import org.example.core.VirtualPoint;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteAnimationManager {
    private final List<Edge> edges;
    private final Map<PointOfInterest, LocalDateTime> arrivalTimes;
    private final List<MapPoint> mappedPoints;
    private final Pane canvas;

    private final Map<MapPoint, Text> pointLabels = new HashMap<>();

    public RouteAnimationManager(Pane canvas, List<Edge> edges, Map<PointOfInterest, LocalDateTime> arrivalTimes, List<MapPoint> mappedPoints) {
        this.edges = edges;
        this.arrivalTimes = arrivalTimes;
        this.mappedPoints = mappedPoints;
        this.canvas = canvas;
    }

    public void drawSegments() {
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            MapPoint startMp = mappedPoints.get(i);
            MapPoint endMp = mappedPoints.get(i + 1);

            LocalDateTime arrival = arrivalTimes.get(edge.getDestination());
            double trafficFactor = edge.getTrafficFactorArrivalTime(arrival);
            String strokeColor = getTrafficColor(trafficFactor);

            Line segmentLine = new Line(startMp.getX(), startMp.getY(), endMp.getX(), endMp.getY());
            segmentLine.setStyle("-fx-stroke: " + strokeColor + "; -fx-stroke-width: 4px; -fx-stroke-linecap: round;");

            canvas.getChildren().add(segmentLine);
        }
    }

    private String getTrafficColor(double trafficFactor) {
        if (trafficFactor <= 1.15) {
            return "#00FF66";
        }

        else if (trafficFactor <= 1.45) {
            return "#FF9900";
        }

        else {
            return "#FF0055";
        }
    }

    public void drawPinsAndTags() {
        for (MapPoint mapPoint : mappedPoints) {
            boolean isCity = mapPoint.getPointOfInterest() instanceof City;
            boolean isVirtualPoint = mapPoint.getPointOfInterest() instanceof VirtualPoint;

            Circle nodePin = new Circle(isCity ? 5.5 : 3.5);

            nodePin.setCenterX(mapPoint.getX());
            nodePin.setCenterY(mapPoint.getY());

            if (isCity) nodePin.getStyleClass().add("map-node-pin-city");
            else nodePin.getStyleClass().add("map-node-pin-poi");

            canvas.getChildren().add(nodePin);

            if (!isVirtualPoint) {
                Text labelText = new Text(mapPoint.getPointOfInterest().getName());
                labelText.setX(mapPoint.getX() + 8);
                labelText.setY(mapPoint.getY() - 8);

                if (isCity) {
                    labelText.getStyleClass().add("map-node-label-city");
                    labelText.setVisible(true);
                }
                else {
                    labelText.getStyleClass().add("map-node-label-poi");
                    labelText.setVisible(false);
                }
                canvas.getChildren().add(labelText);
                pointLabels.put(mapPoint, labelText);
            }
        }
    }

    public void setAnimation(SequentialTransition fullSimulationSequence, double SIMULATION_SPEED_FACTOR) {
        Circle vehicleMarker = new Circle(7);
        vehicleMarker.getStyleClass().add("map-vehicle-marker");

        MapPoint startPoint = mappedPoints.get(0);
        vehicleMarker.setTranslateX(startPoint.getX());
        vehicleMarker.setTranslateY(startPoint.getY());

        canvas.getChildren().add(vehicleMarker);

        fullSimulationSequence.statusProperty().addListener((observable, oldStatus, newStatus) -> {
           if (newStatus == Animation.Status.STOPPED) {
               resetPoiLabelsVisibility();
           }
        });

        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            MapPoint sourceMapPoint = mappedPoints.get(i);
            MapPoint destinationMapPoint = mappedPoints.get(i + 1);

            LocalDateTime startTime = arrivalTimes.get(edge.getSource());
            LocalDateTime endTime = arrivalTimes.get(edge.getDestination());

            long durationMinutes;
            if (startTime != null && endTime != null && startTime.isBefore(endTime)) {
                durationMinutes = Duration.between(startTime, endTime).toMinutes();
            }
            else {
                durationMinutes = 10;
            }

            if (durationMinutes <= 0) {
                durationMinutes = 1;
            }

            Path segPath = new Path();
            segPath.getElements().add(new MoveTo(sourceMapPoint.getX(), sourceMapPoint.getY()));
            segPath.getElements().add(new LineTo(destinationMapPoint.getX(), destinationMapPoint.getY()));

            PathTransition segmentAnim = new PathTransition();
            segmentAnim.setDuration(javafx.util.Duration.millis(durationMinutes * SIMULATION_SPEED_FACTOR));
            segmentAnim.setPath(segPath);
            segmentAnim.setNode(vehicleMarker);

            segmentAnim.statusProperty().addListener((observable, oldStatus, newStatus) -> {
                if (newStatus == Animation.Status.RUNNING) {
                    Text sourceLabel = pointLabels.get(sourceMapPoint);
                    if (sourceLabel != null) {
                        sourceLabel.setVisible(true);
                    }

                    Text destinationLabel = pointLabels.get(destinationMapPoint);
                    if (destinationLabel != null) {
                        destinationLabel.setVisible(true);
                    }
                }
            });

            fullSimulationSequence.getChildren().add(segmentAnim);

            if (i < edges.size() - 1) {
                Edge nextEdge = edges.get(i + 1);
                LocalDateTime nextDepartureTime = arrivalTimes.get(nextEdge.getSource());

                if (endTime != null && nextDepartureTime != null && nextDepartureTime.isAfter(endTime)) {
                    long stopMinutes = Duration.between(endTime, nextDepartureTime).toMinutes();

                    if (stopMinutes > 0) {
                        PauseTransition stopAnim = new PauseTransition(javafx.util.Duration.millis(stopMinutes * SIMULATION_SPEED_FACTOR));
                        fullSimulationSequence.getChildren().add(stopAnim);
                    }
                }
            }
        }
    }

    private void resetPoiLabelsVisibility() {
        for (Map.Entry<MapPoint, Text> entry : pointLabels.entrySet()) {
            boolean isCity = entry.getKey().getPointOfInterest() instanceof City;
            if (!isCity) {
                entry.getValue().setVisible(false);
            }
        }
    }
}