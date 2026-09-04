package org.example.gui.views;

import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.example.calculations.Physics;
import org.example.calculations.PointOfInterestService;
import org.example.calculations.VehicleService;
import org.example.core.City;
import org.example.core.Vehicle;
import org.example.gui.components.MapComponents;
import org.example.session.AppSessionNavigation;
import org.example.gui.utils.AutoCompleteHelper;
import org.example.gui.utils.ColorUtils;
import org.example.gui.utils.Initializer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class NavigationView extends BorderPane {
    private final VehicleService srvVehicle;
    private final PointOfInterestService srvPointOfInterest;
    private Initializer initializer = new Initializer();
    private final Runnable onHomePressed;

    private TextField startLocationInput;
    private TextField destinationLocationInput;
    private Button searchButton;
    Vehicle activeVehicle;

    private City sourceCity;
    private City destinationCity;

    private MapComponents mapComponent;

    public NavigationView(VehicleService srvVehicle, PointOfInterestService srvPointOfInterest, Runnable onHomePressed) {
        this.srvVehicle = srvVehicle;
        this.srvPointOfInterest = srvPointOfInterest;
        this.onHomePressed = onHomePressed;
        initializer.applyCSS("/style/navigation.css", this);
        initializeNavigationViewComponents();
    }

    private void initializeNavigationViewComponents() {
        this.getStyleClass().add("root");

        activeVehicle = AppSessionNavigation.getInstance().getActiveVehicle();
        mapComponent = new MapComponents("map/world.svg");
        Pane mapPane = mapComponent.initializeMapView(-300, 200);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(mapPane.widthProperty());
        clip.heightProperty().bind(mapPane.heightProperty());
        mapPane.setClip(clip);

        VBox controlPanel = initializeControlPanel();
        controlPanel.setOnScroll(Event::consume);
        this.setLeft(controlPanel);
        this.setCenter(mapPane);
    }

    private VBox initializeControlPanel() {
        VBox controlPanel = new VBox();
        controlPanel.setSpacing(20);
        controlPanel.setPadding(new Insets(25));
        controlPanel.setPrefWidth(380);
        controlPanel.getStyleClass().add("control-panel");

        HBox header = initializeHeader();
        VBox search = initializeSearchRoutePanel();
        VBox dateTimePicker = initializeDateTimePicker();

        searchButton = new Button("SEARCH ROUTES");
        searchButton.setMaxWidth(Double.MAX_VALUE);
        searchButton.setPrefHeight(45);
        ColorUtils.updateCustomizeButtonColor(searchButton, activeVehicle.getEngineType());

        controlPanel.getChildren().addAll(header, search, dateTimePicker, searchButton);
        return controlPanel;
    }

    private HBox initializeHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(15);

        Button homeButton = new Button("HOME");
        ColorUtils.updateCustomizeButtonColor(homeButton, activeVehicle.getEngineType());
        homeButton.setOnAction(e -> {
            if (onHomePressed != null) {
                onHomePressed.run();
            }
        });

        Label title = new Label("NAVIGATION SYSTEM");
        title.getStyleClass().add("navigation-system-title");

        header.getChildren().addAll(homeButton, title);
        return header;
    }

    private void putSourceCityOnMap() {
        double[] coordinates = Physics.convertToSvg(sourceCity.getX(), sourceCity.getY());
        mapComponent.setSourcePin(coordinates[0], coordinates[1], sourceCity, "map/pin.png", 5);
    }

    public void putDestinationCityOnMap() {
        double[] coordinates = Physics.convertToSvg(destinationCity.getX(), destinationCity.getY());
        mapComponent.setDestinationPin(coordinates[0], coordinates[1], destinationCity, "map/pin.png", 5);
    }

    private VBox initializeDateTimePicker() {
        VBox dateTimePicker = new VBox();
        dateTimePicker.setSpacing(10);

        Label dateLabel = new Label("START DATE");
        dateLabel.getStyleClass().add("input-label-text");

        DatePicker datePicker = new DatePicker();
       // datePicker.setValue(LocalDate.now());
        datePicker.getStyleClass().add("custom-date-picker");
        TextField timeField = new TextField();

        LocalDate startDate = AppSessionNavigation.getInstance().getStartDate();
        if (startDate != null) {
            datePicker.setValue(startDate);
        }

        LocalTime startTime = AppSessionNavigation.getInstance().getStartTime();
        if (startTime != null) {
            timeField.setText(startTime.toString());
        }

        datePicker.setOnAction(selectedDace -> {
            AppSessionNavigation.getInstance().setStartDate(datePicker.getValue());
        });

        AutoCompleteHelper.setupAutoCompleteTime(timeField, selectedTime -> {
            timeField.setText(selectedTime.toString());
            AppSessionNavigation.getInstance().setStartTime(selectedTime);
        });

        VBox timeBox = initializer.initializeInputGroup(timeField, "START TIME", "ex: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        dateTimePicker.getChildren().addAll(dateLabel, datePicker, timeBox);
        return dateTimePicker;
    }

    private VBox initializeSearchRoutePanel() {
        VBox searchRoutePanel = new VBox();
        searchRoutePanel.setSpacing(10);

        startLocationInput = new TextField();
        destinationLocationInput = new TextField();

        this.sourceCity = AppSessionNavigation.getInstance().getSourceCity();
        this.destinationCity = AppSessionNavigation.getInstance().getDestinationCity();

        if (sourceCity != null) {
            startLocationInput.setText(sourceCity.getStringSearching());
            putSourceCityOnMap();
        }

        if (destinationCity != null) {
            destinationLocationInput.setText(destinationCity.getStringSearching());
            putDestinationCityOnMap();
        }

        AutoCompleteHelper.setupAutoCompleteCity(startLocationInput, srvPointOfInterest, selectedCity -> {
            AppSessionNavigation.getInstance().setSourceCity(selectedCity);
            this.sourceCity = AppSessionNavigation.getInstance().getSourceCity();
            putSourceCityOnMap();
        });

        AutoCompleteHelper.setupAutoCompleteCity(destinationLocationInput, srvPointOfInterest, selectedCity -> {
            AppSessionNavigation.getInstance().setDestinationCity(selectedCity);
            this.destinationCity = AppSessionNavigation.getInstance().getDestinationCity();
            putDestinationCityOnMap();
        });

        VBox startBox = initializer.initializeInputGroup(startLocationInput, "ORIGIN POINT", "ex: Cluj-Napoca, Romania");
        VBox destBox = initializer.initializeInputGroup(destinationLocationInput, "DESTINATION", "ex: Paris, France");

        searchRoutePanel.getChildren().addAll(startBox, destBox);
        return searchRoutePanel;
    }
}