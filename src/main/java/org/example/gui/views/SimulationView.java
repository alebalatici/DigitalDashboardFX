package org.example.gui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import org.example.calculations.PointOfInterestService;
import org.example.calculations.VehicleService;
import org.example.core.City;
import org.example.core.Vehicle;
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

    private Vehicle activeVehicle;
    private City sourceCity;
    private City destinationCity;

    private LocalDateTime startDateTime;

    private final Initializer initializer = new Initializer();

    private VBox logContainer;
    private ScrollPane terminalScrollPane;
    private Label statusLabel;

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

        HBox header = initializeHeader();
        GridPane simulationGrid = initializeSimulationGrid();

        mainContainer.getChildren().addAll(header, simulationGrid);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("custom-scroll-pane");

        mainPane.setCenter(scrollPane);
        this.getChildren().add(mainPane);
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

        String labelText = "";
        String buttonText = "";
        if (sourceCity != null && destinationCity != null && startDateTime != null) {
            labelText = "ROUTE: " + sourceCity.getName() + " -> " + destinationCity.getName();
            buttonText = "CHANGE ROUTE";
        }
        else {
            labelText = "Please select the source city, the destination city and the start date & time";
            buttonText = "GO TO NAVIGATION SECTION";
        }

        Label labelNavigation = new Label(labelText);
        Button buttonNavigation = new Button(buttonText);
        ColorUtils.updateCustomizeButtonColor(buttonNavigation, activeVehicle.getEngineType());
        ColorUtils.updateBadgeColor(labelNavigation, activeVehicle.getEngineType(), "badge-bracket", initializer.getIngeritedClasses("badge-bracket"));

        VBox navigationBox = new VBox(8);
        navigationBox.setAlignment(Pos.TOP_RIGHT);
        navigationBox.getChildren().addAll(labelNavigation, buttonNavigation);

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

        simulationGrid.add(initializeMapPanel(), 0, 0);
        simulationGrid.add(initializeTerminalPanel(), 0, 1);
        simulationGrid.add(initializeTelemetryPanel(), 1, 0);
        simulationGrid.add(initializeControlsPanel(), 1, 1);

        return simulationGrid;
    }

    private VBox initializeMapPanel() {
        VBox mapPanel = new VBox(15);
        mapPanel.getStyleClass().add("control-panel");
        mapPanel.setPadding(new Insets(20));
        mapPanel.setPrefHeight(340);

        Label title = new Label("GEOSPATIAL ROUTE MAP");
        title.getStyleClass().add("card-section-title");

        Pane mapCanvas = new Pane();
        //mapCanvas.setStyle("map-canvas");
        VBox.setVgrow(mapCanvas, Priority.ALWAYS);


        /*
        Polyline routeLine = new Polyline(50.0, 200.0, 150.0, 120.0, 300.0, 160.0, 450.0, 80.0);
        routeLine.setStyle("-fx-stroke: #FF0055; -fx-stroke-width: 2px; -fx-stroke-dash-array: 8 4;");

        Circle vehicleMarker = new Circle(50, 200, 7);
        vehicleMarker.setStyle("-fx-fill: #00F0FF; -fx-effect: dropshadow(three-pass-box, #00F0FF, 10, 0, 0, 0);");
        mapCanvas.getChildren().addAll(routeLine, vehicleMarker);
        */

        mapPanel.getChildren().addAll(title, mapCanvas);
        return mapPanel;
    }

    private VBox initializeTelemetryPanel() {
        VBox telemetryPanel = new VBox(15);
        telemetryPanel.getStyleClass().add("vehicle-card");
        telemetryPanel.setPadding(new Insets(20));
        telemetryPanel.setPrefHeight(340);

        Label title = new Label("VEHICLE LIVE TELEMETRY");
        title.getStyleClass().add("card-section-title");

        HBox gaugesBox = new HBox(30);
        gaugesBox.setAlignment(Pos.CENTER);
        gaugesBox.setPadding(new Insets(10, 0, 10, 0));

        VBox speedBox = createGaugeWidget("SPEED", "0", "km/h");

        VBox rpmBox = createGaugeWidget("ENGINE SPEED", "0", "RPM");

        gaugesBox.getChildren().addAll(speedBox, rpmBox);

        VBox energyBox = initializer.initializeFuelStatus(activeVehicle, "stat-label");

        HBox statusBox = new HBox(15);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        statusLabel = new Label("STATUS: READY");
        //

        statusBox.getChildren().addAll(statusLabel);

        telemetryPanel.getChildren().addAll(title, gaugesBox, energyBox, statusBox);
        return telemetryPanel;
    }

    private VBox createGaugeWidget(String titleText, String initialValue, String unit) {
        VBox box = new VBox(2);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("gauge-widget");

        Label titleLabel = new Label(titleText);
        titleLabel.getStyleClass().add("stat-label");

        Label valueLabel = new Label(initialValue);
        ColorUtils.updateBadgeColor(valueLabel, activeVehicle.getEngineType(), "vehicle-name-label", initializer.getIngeritedClasses("vehicle-name-label"));

        Label unitLabel = new Label(unit);
        unitLabel.getStyleClass().add("stat-label");

        box.getChildren().addAll(titleLabel, valueLabel, unitLabel);
        return box;
    }

    private VBox initializeControlsPanel() {
        VBox controlsPanel = new VBox(15);
        controlsPanel.getStyleClass().add("control-panel");
        controlsPanel.setPadding(new Insets(20));

        Label title = new Label("SIMULATION SETTINGS");
        title.getStyleClass().add("card-section-title");

        Label carTitle = new Label(activeVehicle.getBrand() + "\n" + activeVehicle.getModel());
        ColorUtils.updateBadgeColor(carTitle, activeVehicle.getEngineType(), "customizable-label", initializer.getIngeritedClasses("customizable-label"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button changeVehicleButton = new Button("CHANGE VEHICLE");
        ColorUtils.updateCustomizeButtonColor(changeVehicleButton, activeVehicle.getEngineType());

        changeVehicleButton.setOnMouseClicked(event -> {
            if (onSettingsPressed != null) {
                onSettingsPressed.run();
            }
        });

        HBox vehicleSettingsBox = new HBox(15);
        vehicleSettingsBox.getChildren().addAll(title, carTitle, spacer, changeVehicleButton);

        controlsPanel.getChildren().addAll(title, vehicleSettingsBox);
        return controlsPanel;
    }

    private VBox initializeTerminalPanel() {
        VBox terminalPanel = new VBox(10);
        terminalPanel.getStyleClass().add("control-panel");
        terminalPanel.setPadding(new Insets(20));

        Label title = new Label("REAL-TIME SYSTEM LOGS");
        title.getStyleClass().add("card-section-title");

        logContainer = new VBox(6);
        logContainer.setPadding(new Insets(10));
        logContainer.setStyle("-fx-background-color: #0A0B10;");

        terminalScrollPane = new ScrollPane(logContainer);
        terminalScrollPane.setFitToWidth(true);
        terminalScrollPane.setFitToHeight(true);
        terminalScrollPane.getStyleClass().add("custom-scroll-pane");

        logContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            terminalScrollPane.setVvalue(1.0);
        });

        terminalPanel.getChildren().addAll(title, terminalScrollPane);
        return terminalPanel;
    }
}