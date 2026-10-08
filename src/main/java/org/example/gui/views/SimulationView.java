package org.example.gui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.example.algorithms.Dijkstra;
import org.example.algorithms.PathResult;
import org.example.calculations.PointOfInterestService;
import org.example.calculations.VehicleService;
import org.example.core.City;
import org.example.core.Vehicle;
import org.example.gui.components.simulation_view_components.ControlsPanel;
import org.example.gui.components.simulation_view_components.RouteMapCanvas;
import org.example.gui.components.simulation_view_components.TelemetryPanel;
import org.example.gui.components.simulation_view_components.TerminalLogService;
import org.example.session.AppSessionNavigation;
import org.example.gui.utils.ColorUtils;
import org.example.gui.utils.Initializer;

import java.time.LocalDateTime;

public class SimulationView extends Pane {
    private final VehicleService srvVehicle;
    private final PointOfInterestService srvPointOfInterest;
    private final Runnable onHomePressed;
    private final Runnable onNavigationPressed;
    private final Runnable onSettingsPressed;

    private RouteMapCanvas mapCanvas;

    private final javafx.animation.PauseTransition longDelay = new javafx.animation.PauseTransition(Duration.seconds(1));
    private final javafx.animation.PauseTransition shortDelay = new javafx.animation.PauseTransition(Duration.seconds(0.05));
    private TerminalLogService terminalLogService;

    private Vehicle activeVehicle;
    private City sourceCity;
    private City destinationCity;

    private LocalDateTime startDateTime;

    private final Initializer initializer = new Initializer();

    private VBox logContainer;
    private ScrollPane terminalScrollPane;

    private Button startStopSimulationButton;
    private Button restartSimulationButton;
    private Button refreshButton;

    private HBox simulationButtons;

    private Button buttonNavigation;

    public SimulationView(VehicleService srvVehicle, PointOfInterestService srvPointOfInterest, Runnable onHomePressed, Runnable onNavigationPressed, Runnable onSettingsPressed) {
        this.srvVehicle = srvVehicle;
        this.srvPointOfInterest = srvPointOfInterest;
        this.onHomePressed = onHomePressed;
        this.onNavigationPressed = onNavigationPressed;
        this.onSettingsPressed = onSettingsPressed;
        initializer.applyCSS("/style/simulation.css", this);
        initializeSimulationViewComponents();
    }

    private void initializeSimulationViewComponents() {
        BorderPane mainPane = new BorderPane();

        mainPane.prefWidthProperty().bind(this.widthProperty());
        mainPane.prefHeightProperty().bind(this.heightProperty());

        mainPane.getStyleClass().add("root");

        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(25));

        activeVehicle = AppSessionNavigation.getInstance().getActiveVehicle();
        sourceCity = AppSessionNavigation.getInstance().getSourceCity();
        destinationCity = AppSessionNavigation.getInstance().getDestinationCity();
        startDateTime = AppSessionNavigation.getInstance().getStartDateTime();

        startStopSimulationButton = new Button("START SIMULATION");
        ColorUtils.updateSimulationButtonColor(startStopSimulationButton, activeVehicle.getEngineType());

        restartSimulationButton = new Button("RESTART SIMULATION");
        restartSimulationButton.setVisible(false);
        restartSimulationButton.setManaged(false);
        ColorUtils.updateSimulationButtonColor(restartSimulationButton, activeVehicle.getEngineType());

        simulationButtons = new HBox(10);
        simulationButtons.setAlignment(Pos.CENTER_LEFT);
        simulationButtons.getChildren().addAll(startStopSimulationButton, restartSimulationButton);

        HBox header = initializeHeader();
        GridPane simulationGrid = initializeSimulationGrid();

        mainContainer.getChildren().addAll(header, simulationButtons, simulationGrid);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("custom-scroll-pane");

        mainPane.setCenter(scrollPane);
        this.getChildren().add(mainPane);

        computeRoute();
    }

    private HBox initializeHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Button homeButton = new Button("HOME");
        ColorUtils.updateCustomizeButtonColor(homeButton, activeVehicle.getEngineType());

        homeButton.setOnAction(e -> {
            if (onHomePressed != null) {
                onHomePressed.run();
            }
        });

        Label title = new Label("LIVE TRIP SIMULATION");
        title.getStyleClass().add("navigation-system-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        StringBuilder labelText = new StringBuilder();
        String buttonText = "";
        if (sourceCity != null && destinationCity != null && startDateTime != null) {
            labelText.append("ROUTE: ").append(sourceCity.getName()).append(" -> ").append(destinationCity.getName());
            labelText.append("\n").append(startDateTime.getDayOfWeek()).append("\n").append(startDateTime.toLocalTime().toString()).append("\n").append(startDateTime.toLocalDate().toString());
            buttonText = "CHANGE ROUTE";
            simulationButtons.setVisible(true);
            simulationButtons.setManaged(true);
        }
        else {
            if (sourceCity == null) {
                labelText.append("The source city is missing.\n");
            }

            if (destinationCity == null) {
                labelText.append("The destination city is missing.\n");
            }

            if (startDateTime == null) {
                labelText.append("The start date & time are missing.\n");
            }
            buttonText = "GO TO NAVIGATION SECTION";
            simulationButtons.setVisible(false);
            simulationButtons.setManaged(false);
        }

        Label labelNavigation = new Label(labelText.toString());
        buttonNavigation = new Button(buttonText);
        ColorUtils.updateCustomizeButtonColor(buttonNavigation, activeVehicle.getEngineType());
        ColorUtils.updateBadgeColor(labelNavigation, activeVehicle.getEngineType(), "badge-bracket", initializer.getIngeritedClasses("badge-bracket"));

        VBox navigationBox = new VBox(8);
        navigationBox.setAlignment(Pos.TOP_RIGHT);
        navigationBox.getChildren().addAll(labelNavigation);

        buttonNavigation.setOnMouseClicked(e -> {
            if (onNavigationPressed != null) {
                onNavigationPressed.run();
            }
        });

        header.getChildren().addAll(homeButton, title, spacer, navigationBox);
        return header;
    }

    private GridPane initializeSimulationGrid() {
        GridPane simulationGrid = new GridPane();
        simulationGrid.setHgap(20);
        simulationGrid.setVgap(20);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(60);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(45);
        simulationGrid.getColumnConstraints().addAll(column1, column2);

        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(55);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(40);
        simulationGrid.getRowConstraints().addAll(row1, row2);

        VBox terminalPanel = initializeTerminalPanel();
        VBox mapPanel = initializeMapPanel();

        TelemetryPanel telemetryPanel = new TelemetryPanel(activeVehicle, initializer);
        VBox telemetryPanelBox = telemetryPanel.initializeTelemetryPanel();
        VBox controlsPanel = initializeControlsPanel();

        simulationGrid.add(mapPanel, 0, 0);
        simulationGrid.add(terminalPanel, 0, 1);
        simulationGrid.add(telemetryPanelBox, 1, 0);
        simulationGrid.add(controlsPanel, 1, 1);

        return simulationGrid;
    }

    private void computeRoute() {
        if (sourceCity != null && destinationCity != null && startDateTime != null && activeVehicle != null) {
            terminalLogService.log("INITIALIZING ROUTE CALCULATION", TerminalLogService.COLOR_DEFAULT);
            terminalLogService.log("ORIGIN: " + sourceCity.getStringSearching().toUpperCase(), TerminalLogService.COLOR_DEFAULT);
            terminalLogService.log("DESTINATION: " + destinationCity.getStringSearching().toUpperCase(), TerminalLogService.COLOR_DEFAULT);

            terminalLogService.log("RUNNING DIJKSTRA...", TerminalLogService.COLOR_INFO);
            longDelay.play();

            PathResult pathResult = Dijkstra.dijkstra(
                    srvPointOfInterest.getGraph().getAdjacencyList(),
                    sourceCity,
                    destinationCity,
                    startDateTime,
                    activeVehicle
            );

            if (pathResult == null || pathResult.getPath() == null || pathResult.getPath().isEmpty()) {
                String warningMsg = "NO VALID ROUTE FOUND BETWEEN " + sourceCity.getName().toUpperCase() + " AND " + destinationCity.getName().toUpperCase();
                mapCanvas.showWarningMessage(warningMsg, "TIP: Increase the GRAPH CONECTIVITY RANGE from SIMULATION SETTINGS and click TRY AGAIN", activeVehicle, initializer);
                terminalLogService.log("ROUTE COMPUTATION FAILED", TerminalLogService.COLOR_ERROR);
                refreshButton.setVisible(true);
                refreshButton.setManaged(true);
            } else {
                mapCanvas.setRoute(pathResult);
                terminalLogService.log("OPTIMAL PATH FOUND", TerminalLogService.COLOR_SUCCESS);
                shortDelay.play();
                terminalLogService.log("TOTAL POINTS VISITED: " + pathResult.getPointsOfInterest().size(), TerminalLogService.COLOR_INFO);
                terminalLogService.log("TOTAL DISTANCE: " + pathResult.getTotalKm(), TerminalLogService.COLOR_INFO);
                //terminalLogService.log("TOTAL TIME: " + pathResult.);
            }
        }

        else {
            mapCanvas.showWarningMessage("PLEASE SELECT THE JOURNEY PARAMETERS", activeVehicle, initializer);
        }

    }

    private VBox initializeMapPanel() {
        VBox mapPanel = new VBox(15);
        mapPanel.getStyleClass().add("control-panel");
        mapPanel.setPadding(new Insets(20));
        mapPanel.setPrefHeight(340);

        Label title = new Label("GEOSPATIAL ROUTE MAP");
        title.getStyleClass().add("card-section-title");

        mapCanvas = new RouteMapCanvas(startStopSimulationButton, restartSimulationButton, terminalLogService);
        VBox.setVgrow(mapCanvas, Priority.ALWAYS);

        refreshButton = new Button("TRY AGAIN");
        ColorUtils.updateCustomizeButtonColor(refreshButton, activeVehicle.getEngineType());
        refreshButton.setVisible(false);
        refreshButton.setManaged(false);
        refreshButton.setOnAction(event -> {
            computeRoute();
        });

        HBox buttonNavigationRefreshBox = new HBox(10);
        buttonNavigationRefreshBox.setAlignment(Pos.CENTER_LEFT);
        buttonNavigationRefreshBox.getChildren().addAll(buttonNavigation, refreshButton);

        mapPanel.getChildren().addAll(title, mapCanvas, buttonNavigationRefreshBox);
        return mapPanel;
    }

    private VBox initializeControlsPanel() {
        VBox controlsPanelBox = new VBox(15);
        controlsPanelBox.getStyleClass().add("control-panel");
        controlsPanelBox.setPadding(new Insets(20));

        Label title = new Label("SIMULATION SETTINGS");
        title.getStyleClass().add("card-section-title");
        title.getStyleClass().add("card-section-title");

        ControlsPanel controlsPanel = new ControlsPanel(initializer, srvPointOfInterest, activeVehicle, onSettingsPressed);
        HBox vehicleSettingsBox = controlsPanel.initializeCarSettingsEditGroup();
        VBox graphMaxDistanceBox = controlsPanel.initializeGraphMaxDistanceEditGroup();

        controlsPanelBox.getChildren().addAll(title, vehicleSettingsBox, graphMaxDistanceBox);
        return controlsPanelBox;
    }

    private VBox initializeTerminalPanel() {
        VBox terminalPanel = new VBox(10);
        terminalPanel.getStyleClass().add("control-panel");
        terminalPanel.setPadding(new Insets(20));

        Label title = new Label("SYSTEM LOGS");
        title.getStyleClass().add("card-section-title");

        logContainer = new VBox(6);
        logContainer.setPadding(new Insets(10));
        logContainer.setStyle("-fx-background-color: #0A0B10;");

        terminalScrollPane = new ScrollPane(logContainer);
        terminalScrollPane.setFitToWidth(true);
        terminalScrollPane.setFitToHeight(true);
        terminalScrollPane.getStyleClass().add("custom-scroll-pane");

        terminalScrollPane.setMinHeight(180);
        terminalScrollPane.setPrefHeight(220);
        terminalScrollPane.setMaxHeight(250);
        VBox.setVgrow(terminalScrollPane, Priority.ALWAYS);

        terminalLogService = new TerminalLogService(logContainer, terminalScrollPane);
        terminalLogService.log("SYSTEM LOGS INITIALIZED. READY.", TerminalLogService.COLOR_INFO);

        terminalPanel.getChildren().addAll(title, terminalScrollPane);
        return terminalPanel;
    }
}