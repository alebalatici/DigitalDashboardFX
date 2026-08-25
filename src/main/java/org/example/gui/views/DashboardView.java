package org.example.gui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.example.calculations.PointOfInterestService;
import org.example.calculations.VehicleService;
import org.example.core.City;
import org.example.core.Vehicle;
import org.example.gui.utils.AppSession;
import org.example.gui.utils.ColorUtils;
import org.example.gui.utils.Initializer;

import java.util.List;

public class DashboardView extends BorderPane {
    private final VehicleService srvVehicle;
    private final PointOfInterestService srvPointOfInterest;
    private final Initializer initializer = new Initializer();

    private Vehicle activeVehicle;

    private City sourceCity;
    private City destinationCity;

    public DashboardView(VehicleService srvVehicle, PointOfInterestService srvPointOfInterest) {
        this.srvVehicle = srvVehicle;
        this.srvPointOfInterest = srvPointOfInterest;
        initializer.applyCSS("/style/dashboard_menu.css", this);
        initializeDashboardViewComponents();
    }

    private void initializeDashboardViewComponents() {
        this.getStyleClass().add("root");
        this.setTop(initializeTopHeader());
        this.setCenter(initializeMainDashboardLayout());
        this.setBottom(initializeFooter());
    }

    private HBox initializeTopHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("hud-header-container");
        header.setAlignment(Pos.CENTER_LEFT);

        Label systemTitle = new Label("SYSTEM CONTROL CENTER");
        systemTitle.getStyleClass().add("system-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox dateTimeBox = new VBox(2);
        dateTimeBox.setAlignment(Pos.CENTER_RIGHT);

        Label timeLabel = new Label("00:00:00");
        timeLabel.getStyleClass().add("time-label");

        HBox statusLine = new HBox(10);
        statusLine.setAlignment(Pos.CENTER_RIGHT);

        Label statusDot = new Label("ONLINE");
        statusDot.getStyleClass().add("system-status-indicator");

        Label dateLabel = new Label("SYSTEM READY");
        dateLabel.getStyleClass().add("date-label");

        statusLine.getChildren().addAll(statusDot, dateLabel);
        dateTimeBox.getChildren().addAll(timeLabel, statusLine);

        initializer.initializeDateTimeLabels(timeLabel, dateLabel);
        header.getChildren().addAll(systemTitle, spacer, dateTimeBox);
        return header;
    }

    private GridPane initializeMainDashboardLayout() {
        GridPane mainGrid = new GridPane();
        mainGrid.setAlignment(Pos.CENTER);
        mainGrid.setHgap(20);
        mainGrid.setVgap(20);
        mainGrid.setPadding(new Insets(25));


        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(32);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(34);

        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPercentWidth(34);

        mainGrid.getColumnConstraints().addAll(column1, column2, column3);


        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(50);

        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(50);

        mainGrid.getRowConstraints().addAll(row1, row2);

        if (AppSession.getInstance().getActiveVehicle() == null) {
            AppSession.getInstance().setActiveVehicle(srvVehicle.getRandomVehicle());
        }

        VBox activeVehicleCard = initializeActiveVehiclePanel();
        mainGrid.add(activeVehicleCard, 0, 0, 1, 2);

        VBox routeCard = initializeRouteCard();
        VBox speedCard = initializeSimulationCard();
        VBox mapCard = initializeMapCard();
        VBox settingsCard = initializeSettingsCard();

        mainGrid.add(routeCard, 1, 0);
        mainGrid.add(speedCard, 1, 1);
        mainGrid.add(settingsCard, 2, 0);
        mainGrid.add(mapCard, 2, 1);
        return mainGrid;
    }

    private VBox initializeActiveVehiclePanel() {
        VBox card = new VBox(15);
        card.getStyleClass().add("vehicle-info-card");

        activeVehicle = AppSession.getInstance().getActiveVehicle();

        Label sectionTag = new Label("ACTIVE VEHICLE");
        sectionTag.getStyleClass().add("module-tag");

        Label carName = new Label(activeVehicle.getBrand() + "\n" + activeVehicle.getModel());
        ColorUtils.updateBadgeColor(carName, activeVehicle.getEngineType(), "vehicle-name-label", List.of("vehicle-name-label-electric", "vehicle-name-label-gasoline", "vehicle-name-label-diesel"));

        Label engineBadge = new Label(activeVehicle.getStringEngineType());
        ColorUtils.updateBadgeColor(engineBadge, activeVehicle.getEngineType(), "badge", List.of("badge-electric", "badge-gasoline", "badge-diesel"));

        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        headerBox.getChildren().addAll(carName, spacer, engineBadge);

        VBox energyBox = initializer.initializeFuelStatus(activeVehicle, "module-tag1");

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(15);
        statsGrid.setVgap(15);
        statsGrid.setPadding(new Insets(10, 0, 0, 0));

        statsGrid.add(initializer.createStatItem("YEAR", String.valueOf(activeVehicle.getReleaseYear()), "module-tag", "stat-value-label", List.of("stat-value-label-electric", "stat-value-label-gasoline", "stat-value-label-diesel"), activeVehicle), 0, 0);
        statsGrid.add(initializer.createStatItem("TOTAL KILOMETERS", activeVehicle.getTotalKilometres() + " km", "module-tag", "stat-value-label", List.of("stat-value-label-electric", "stat-value-label-gasoline", "stat-value-label-diesel"), activeVehicle), 1, 0);

        Region spacer1 = new Region();
        VBox.setVgrow(spacer1, Priority.ALWAYS);

        card.getChildren().addAll(sectionTag, headerBox, energyBox, statsGrid, spacer1);
        return card;
    }

    public VBox createCard(String moduleTag, String titleText, String subtitleText) {
        VBox card = new VBox(12);
        card.getStyleClass().add("menu-card");

        Label tag = new Label(moduleTag);
        tag.getStyleClass().add("module-tag");

        Label title = new Label(titleText);
        ColorUtils.updateBadgeColor(title, activeVehicle.getEngineType(), "vehicle-name-label", List.of("vehicle-name-label-electric", "vehicle-name-label-gasoline", "vehicle-name-label-diesel"));

        Label subtitle = new Label(subtitleText);
        subtitle.getStyleClass().add("menu-card-subtitle");
        subtitle.setWrapText(true);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(tag, title, subtitle, spacer);
        return card;
    }

    private VBox initializeSettingsCard() {
        VBox settingsCard = createCard("SYS-02", "SETTINGS", "Select a different vehicle");
        settingsCard.setOnMouseClicked(event -> {
            var currentScene = settingsCard.getScene();
            navigateToSettings(currentScene);
        });

        return settingsCard;
    }

    private VBox initializeMapCard() {
        VBox mapCard = createCard("MAP-04", "MAP", "Explore all registered cities and points of interest");

        mapCard.setOnMouseClicked(event -> {
            var currentScene = mapCard.getScene();
            MapView mapView = new MapView(srvPointOfInterest,
                    () -> currentScene.setRoot(new DashboardView(srvVehicle, srvPointOfInterest))
            );
            currentScene.setRoot(mapView);
        });

        return mapCard;
    }

    private VBox initializeSimulationCard() {
        VBox simulationCard = createCard("SYS-03", "SIMULATION", "Start the simulation");

        simulationCard.setOnMouseClicked(event -> {
            var currentScene = simulationCard.getScene();
            SimulationView simulationView = new SimulationView(srvVehicle, srvPointOfInterest,
                    () -> currentScene.setRoot(new DashboardView(srvVehicle, srvPointOfInterest)),
                    () -> navigateToNavigation(currentScene),
                    () -> navigateToSettings(currentScene)
            );
            currentScene.setRoot(simulationView);
        });
        return simulationCard;
    }

    private VBox initializeRouteCard() {
        this.sourceCity = AppSession.getInstance().getSourceCity();
        this.destinationCity = AppSession.getInstance().getDestinationCity();

        String subtitleText = "";
        if (sourceCity != null) {
            subtitleText = sourceCity.getName() + " -> " + destinationCity.getName() + "\nLinear distance: " + (int) Math.ceil(srvPointOfInterest.HavesineDistance(sourceCity, destinationCity)) + " km";
        }

        else {
            subtitleText = "Begin a journey";
        }

        VBox routeCard = createCard("NAV-01", "NAVIGATION", subtitleText);

        routeCard.setOnMouseClicked(event -> {
            navigateToNavigation(routeCard.getScene());
        });
        return routeCard;
    }

    private void navigateToNavigation(Scene currentScene) {
        NavigationView navigationView = new NavigationView(
                srvVehicle,
                srvPointOfInterest,
                () -> currentScene.setRoot(new DashboardView(srvVehicle, srvPointOfInterest))
        );
        currentScene.setRoot(navigationView);
    }

    private void navigateToSettings(Scene currentScene) {
        SettingsView settingsView = new SettingsView(srvVehicle,
                () -> currentScene.setRoot(new DashboardView(srvVehicle, srvPointOfInterest))
        );
        currentScene.setRoot(settingsView);
    }

    private HBox initializeFooter() {
        HBox footer = new HBox();
        footer.getStyleClass().add("hud-footer");
        footer.setAlignment(Pos.CENTER_LEFT);

        Label creditsLabel = new Label("DEVELOPED WITH ❤ BY ALEXANDRA");
        creditsLabel.getStyleClass().add("footer-credits");

        String uriString = "https://github.com/alebalatici";
        Tooltip githubTooltip = new Tooltip(uriString);
        githubTooltip.setShowDelay(Duration.millis(200));
        githubTooltip.getStyleClass().add("hud-tooltip");
        Tooltip.install(creditsLabel, githubTooltip);

        creditsLabel.setOnMouseClicked(event -> {
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(uriString));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        footer.getChildren().addAll(spacer, creditsLabel);
        return footer;
    }
}