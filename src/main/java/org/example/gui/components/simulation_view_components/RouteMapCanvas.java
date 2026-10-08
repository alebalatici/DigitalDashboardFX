package org.example.gui.components.simulation_view_components;

import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.example.algorithms.PathResult;
import org.example.calculations.Edge;
import org.example.core.PointOfInterest;
import org.example.core.Vehicle;
import org.example.gui.utils.ColorUtils;
import org.example.gui.utils.Initializer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RouteMapCanvas extends Pane {
    private SequentialTransition fullSimulationSequence;
    private PathResult currentPathResult;

    private final Button actionButton;
    private final Button restartButton;
    private boolean isRunning = false;
    private final TerminalLogService terminalLogService;

    private static final double SIMULATION_SPEED_FACTOR = 30.0;

    private String warningMessageText = null;

    private RouteAnimationManager routeAnimationManager;

    public RouteMapCanvas(Button actionButton, Button restartButton, TerminalLogService terminalLogService) {
        this.actionButton = actionButton;
        this.restartButton = restartButton;
        this.terminalLogService = terminalLogService;

        widthProperty().addListener((observable, oldValue, newValue) -> {
            renderRoute();
        });

        heightProperty().addListener((observable, oldValue, newValue) -> {
            renderRoute();
        });
    }

    public void setRoute(PathResult pathResult) {
        this.warningMessageText = null;
        this.currentPathResult = pathResult;
        Platform.runLater(this::renderRoute);
    }

    public void startSimulation() {
        if (fullSimulationSequence != null) {
            fullSimulationSequence.play();
            isRunning = true;
            if (actionButton != null) {
                actionButton.setText("PAUSE SIMULATION");
            }

            if (restartButton != null) {
                restartButton.setVisible(false);
                restartButton.setManaged(false);
            }
        }
    }

    public void pauseSimulation() {
        if (fullSimulationSequence != null) {
            fullSimulationSequence.pause();
            isRunning = false;
            if (actionButton != null) {
                actionButton.setText("RESUME SIMULATION");
            }

            if (restartButton != null) {
                restartButton.setVisible(true);
                restartButton.setManaged(true);
            }
        }
    }

    public void resetSimulation() {
        if (fullSimulationSequence != null) {
            fullSimulationSequence.stop();
            fullSimulationSequence.jumpTo(Duration.ZERO);

            if (routeAnimationManager != null) {
                routeAnimationManager.resetMarkerPosition();
            }

            isRunning = false;
            if (actionButton != null) {
                actionButton.setText("START SIMULATION");
            }

            if (restartButton != null) {
                restartButton.setVisible(false);
                restartButton.setManaged(false);
            }
        }
    }

    public void showWarningMessage(String message, Vehicle activeVehicle, Initializer initializer) {
        this.warningMessageText = message;
        Platform.runLater(() -> {
            this.getChildren().clear();
            if (fullSimulationSequence != null) {
                fullSimulationSequence.stop();
            }

            Label infoLabel = new Label(message);
            ColorUtils.updateBadgeColor(infoLabel, activeVehicle.getEngineType(), "customizable-label", initializer.getIngeritedClasses("vehicle-name-label"));

            StackPane warningContainer = new StackPane(infoLabel);
            warningContainer.setAlignment(Pos.BOTTOM_LEFT);

            warningContainer.prefWidthProperty().bind(widthProperty());
            warningContainer.prefHeightProperty().bind(heightProperty());

            this.getChildren().add(warningContainer);
        });
    }

    private void renderRoute() {
        if (warningMessageText != null) {
            return;
        }

        this.getChildren().clear();

        if (fullSimulationSequence != null) {
            fullSimulationSequence.stop();
        }

        double width = getWidth();
        double height = getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        List<Edge> edges = currentPathResult.getPath();
        Map<PointOfInterest, LocalDateTime> arrivalTimes = currentPathResult.getArrivalTimes();

        double padding = 60.0;
        double usableWidth = width - (2 * padding);
        double usableHeight = height - (2 * padding);

        List<PointOfInterest> points = currentPathResult.getPointsOfInterest();
        if (points.isEmpty()) {
            return;
        }

        BoundingBox box = BoundingBox.from(points);

        List<MapPoint> mappedPoints = new ArrayList<>();
        for (PointOfInterest poi : points) {
            double x = padding + box.getNormalizedX(poi.getY()) * usableWidth;
            double y = (height - padding) - box.getNormalizedY(poi.getX()) * usableHeight;

            mappedPoints.add(new MapPoint(poi, x, y));
        }

        routeAnimationManager = new RouteAnimationManager(this, edges, arrivalTimes, mappedPoints, terminalLogService);
        routeAnimationManager.drawSegments();
        routeAnimationManager.drawPinsAndTags();

        fullSimulationSequence = new SequentialTransition();

        fullSimulationSequence.setOnFinished(event -> {
            this.isRunning = false;
            if (actionButton != null) {
                Platform.runLater(() -> actionButton.setText("START SIMULATION"));
            }

            if (restartButton != null) {
                restartButton.setVisible(false);
                restartButton.setManaged(false);
            }
        });

        routeAnimationManager.setAnimation(fullSimulationSequence, SIMULATION_SPEED_FACTOR);
        setControls();
    }

    private void setControls() {
        actionButton.setOnAction(event -> {
            if (!isRunning) {
                startSimulation();
            }
            else {
                pauseSimulation();
            }
        });

        if (restartButton != null) {
            restartButton.setOnAction(event -> {
                resetSimulation();
            });
        }
    }
}