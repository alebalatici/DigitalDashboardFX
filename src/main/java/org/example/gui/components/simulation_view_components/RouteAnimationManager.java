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
import org.example.gui.views.SimulationView;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RouteAnimationManager
{
    private final List<Edge> edges;
    private final Map<PointOfInterest, LocalDateTime> arrivalTimes;
    private final List<MapPoint> mappedPoints;
    private final Pane canvas;
    private final TerminalLogService terminalLogService;

    private final Map<PointOfInterest, Text> pointLabels = new HashMap<>();
    private Circle vehicleMarker;

    public RouteAnimationManager(Pane canvas, List<Edge> edges, Map<PointOfInterest, LocalDateTime> arrivalTimes, List<MapPoint> mappedPoints, TerminalLogService terminalLogService) {
        this.edges = edges;
        this.arrivalTimes = arrivalTimes;
        this.mappedPoints = mappedPoints;
        this.canvas = canvas;
        this.terminalLogService = terminalLogService;
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
        pointLabels.clear();
        for (MapPoint mapPoint : mappedPoints) {
            PointOfInterest poi = mapPoint.getPointOfInterest();
            boolean isCity = poi instanceof City;
            boolean isVirtualPoint = poi instanceof VirtualPoint;

            Circle nodePin = new Circle(isCity ? 5.5 : 3.5);
            nodePin.setCenterX(mapPoint.getX());
            nodePin.setCenterY(mapPoint.getY());

            if (isCity) nodePin.getStyleClass().add("map-node-pin-city");
            else nodePin.getStyleClass().add("map-node-pin-poi");

            canvas.getChildren().add(nodePin);

            if (!isVirtualPoint) {
                Text labelText = new Text(poi.getName());
                labelText.setX(mapPoint.getX() + 8);
                labelText.setY(mapPoint.getY() - 8);

                if (isCity) {
                    labelText.getStyleClass().add("map-node-label-city");
                    labelText.setVisible(true);
                } else {
                    labelText.getStyleClass().add("map-node-label-poi");
                    labelText.setVisible(false);
                }
                canvas.getChildren().add(labelText);
                pointLabels.put(poi, labelText);
            }
        }
    }

    public void resetMarkerPosition() {
        if (vehicleMarker != null && !mappedPoints.isEmpty()) {
            MapPoint startPoint = mappedPoints.get(0);
            vehicleMarker.setTranslateX(startPoint.getX());
            vehicleMarker.setTranslateY(startPoint.getY());
        }
    }

    private void setupMarker() {
        vehicleMarker = new Circle(7);
        vehicleMarker.getStyleClass().add("map-vehicle-marker");

        MapPoint startPoint = mappedPoints.get(0);
        vehicleMarker.setTranslateX(startPoint.getX());
        vehicleMarker.setTranslateY(startPoint.getY());

        canvas.getChildren().add(vehicleMarker);
    }

    private void logNewEdge(PauseTransition pauseLogAnim, LocalDateTime startTime, MapPoint sourceMapPoint, MapPoint destinationMapPoint) {
        if (pauseLogAnim != null) {
            pauseLogAnim.setOnFinished(event -> {
                resetPoiLabelsVisibility();

                Text sourceLabel = pointLabels.get(sourceMapPoint.getPointOfInterest());
                if (sourceLabel != null) {
                    sourceLabel.setVisible(true);
                }

                if (terminalLogService != null) {
                  //  String msg = sourceMapPoint.getPointOfInterest().getName() + " " + destinationMapPoint.getPointOfInterest().getName() + " -> " + destinationMapPoint.getPointOfInterest().getName();

                    boolean isCity = sourceMapPoint.getPointOfInterest() instanceof City;
                    boolean isVirtualPoint = sourceMapPoint.getPointOfInterest() instanceof VirtualPoint;
                    StringBuilder msg = new StringBuilder();
                    msg.append("REACHED ").append(sourceMapPoint.getPointOfInterest().getName());
                    if (!isVirtualPoint) {
                        msg.append(", ").append(sourceMapPoint.getPointOfInterest().getCountry());
                    }
                    String colorHex;
                    if (isCity) {
                        colorHex = TerminalLogService.COLOR_DEFAULT;
                    }

                    else {
                        colorHex = TerminalLogService.COLOR_MUTED;
                    }
                    terminalLogService.logSimulatedEvent(startTime, msg.toString(), colorHex);
                }
            });
        }
    }

    public void setAnimation(SequentialTransition fullSimulationSequence, double SIMULATION_SPEED_FACTOR) {
        setupMarker();
        fullSimulationSequence.statusProperty().addListener((observable, oldStatus, newStatus) -> {
            if (newStatus == Animation.Status.STOPPED) {
                resetPoiLabelsVisibility();
                if (terminalLogService != null) {
                    terminalLogService.log("SIMULATION RESET TO START POINT.", TerminalLogService.COLOR_DEFAULT);
                }
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

            PauseTransition pauseLogAnim = new PauseTransition(javafx.util.Duration.millis(1));
            logNewEdge(pauseLogAnim, startTime, sourceMapPoint, destinationMapPoint);
            fullSimulationSequence.getChildren().add(pauseLogAnim);

            Path segPath = new Path();
            segPath.getElements().add(new MoveTo(sourceMapPoint.getX(), sourceMapPoint.getY()));
            segPath.getElements().add(new LineTo(destinationMapPoint.getX(), destinationMapPoint.getY()));

            PathTransition segmentAnim = new PathTransition();
            segmentAnim.setDuration(javafx.util.Duration.millis(durationMinutes * SIMULATION_SPEED_FACTOR));
            segmentAnim.setPath(segPath);
            segmentAnim.setNode(vehicleMarker);

            fullSimulationSequence.getChildren().add(segmentAnim);

            fullSimulationSequence.setOnFinished(event -> {
                if (terminalLogService != null && !mappedPoints.isEmpty()) {
                    MapPoint lastPoint = mappedPoints.get(mappedPoints.size() - 1);
                    LocalDateTime finalArrivalTime = arrivalTimes.get(lastPoint.getPointOfInterest());

                    terminalLogService.logSimulatedEvent(
                            finalArrivalTime, "JOURNEY COMPLETED AT " + lastPoint.getPointOfInterest().getName().toUpperCase(), TerminalLogService.COLOR_SUCCESS);
                }
            });
        }
    }

    private void resetPoiLabelsVisibility() {
        for (Map.Entry<PointOfInterest, Text> entry : pointLabels.entrySet()) {
            boolean isCity = entry.getKey() instanceof City;
            if (!isCity) {
                entry.getValue().setVisible(false);
            }
        }
    }
}