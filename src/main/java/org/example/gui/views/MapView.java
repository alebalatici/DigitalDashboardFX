package org.example.gui.views;

import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import org.example.calculations.Physics;
import org.example.calculations.PointOfInterestService;

import javafx.scene.control.Button;
import org.example.core.*;
import org.example.gui.components.MapComponents;
import org.example.gui.utils.AppSessionGUI;
import org.example.gui.utils.ColorUtils;

public class MapView extends BorderPane {
    private final PointOfInterestService srvPointOfInterest;
    private final Runnable onHomePressed;

    private MapComponents mapComponent;

    public MapView(PointOfInterestService srvPointOfInterest, Runnable onHomePressed) {
        this.srvPointOfInterest = srvPointOfInterest;
        this.onHomePressed = onHomePressed;
        initializeMapViewComponents();
    }

    private void initializeMapViewComponents() {
        this.getStyleClass().add("root");

        mapComponent = new MapComponents("map/world.svg");
        Pane mapPane = mapComponent.initializeMapView(-300, 200);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(mapPane.widthProperty());
        clip.heightProperty().bind(mapPane.heightProperty());
        mapPane.setClip(clip);

        HBox menuBox = initializeBottom();
        menuBox.setOnScroll(Event::consume);

        this.setBottom(menuBox);
        this.setCenter(mapPane);
    }

    private HBox initializeBottom() {
        HBox bottom = new HBox();
        bottom.setSpacing(10);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(10, 10, 30, 10));

        Button homeButton = new Button("HOME");

        Vehicle activeVehicle = AppSessionGUI.getInstance().getActiveVehicle();
        ColorUtils.updateCustomizeButtonColor(homeButton, activeVehicle.getEngineType());
        homeButton.setOnAction(e -> {
            if (onHomePressed != null) {
                onHomePressed.run();
            }
        });

        Button filterCitiesButton = new Button("CITIES");
        filterCitiesButton.getStyleClass().add("map-filter-button");
        filterCitiesButton.setOnAction(e -> {
            mapComponent.clearActivePins();
            showCities(4);
        });

        Button filterGasStationsButton = new Button("GAS STATIONS");
        filterGasStationsButton.getStyleClass().add("map-filter-button");
        filterGasStationsButton.setOnAction(e -> {
            mapComponent.clearActivePins();
            showGasStations(4);
        });

        Button filterHotelsButton = new Button("HOTELS");
        filterHotelsButton.getStyleClass().add("map-filter-button");
        filterHotelsButton.setOnAction(e -> {
            mapComponent.clearActivePins();
            showHotels(4);
        });

        Button filterRestaurantsButton = new Button("RESTAURANTS");
        filterRestaurantsButton.getStyleClass().add("map-filter-button");
        filterRestaurantsButton.setOnAction(e -> {
            mapComponent.clearActivePins();
            showRestaurants(4);
        });

        Button showAll = new Button("SHOW ALL");
        showAll.getStyleClass().add("map-filter-button");
        showAll.setOnAction(e -> {
            mapComponent.clearActivePins();
            showCities(2);
            showGasStations(2);
            showHotels(2);
            showRestaurants(2);
        });

        bottom.getChildren().addAll(homeButton, filterCitiesButton, filterGasStationsButton, filterHotelsButton, filterRestaurantsButton, showAll);
        return bottom;
    }

    private void showCities(double widthAndHeight) {
        for (City city : srvPointOfInterest.getOnlyCities()) {
            double[] coordinates = Physics.convertToSvg(city.getX(), city.getY());
            mapComponent.addPinImagePointOfInterest(coordinates[0], coordinates[1], city, "map/city.png", widthAndHeight);
        }
    }

    private void showGasStations(double widthAndHeight) {
        for (GasStation station : srvPointOfInterest.getOnlyGasStations()) {
            double[] coordinates = Physics.convertToSvg(station.getX(), station.getY());
            mapComponent.addPinImagePointOfInterest(coordinates[0], coordinates[1], station, "map/gas_station.png", widthAndHeight);
        }
    }

    private void showHotels(double widthAndHeight) {
        for (Hotel hotel : srvPointOfInterest.getOnlyHotels()) {
            double[] coordinates = Physics.convertToSvg(hotel.getX(), hotel.getY());
            mapComponent.addPinImagePointOfInterest(coordinates[0], coordinates[1], hotel, "map/hotel.png", widthAndHeight);
        }
    }

    private void showRestaurants(double widthAndHeight) {
        for (Restaurant restaurant : srvPointOfInterest.getOnlyRestaurants()) {
            double[] coordinates = Physics.convertToSvg(restaurant.getX(), restaurant.getY());
            mapComponent.addPinImagePointOfInterest(coordinates[0], coordinates[1], restaurant, "map/restaurant.png", widthAndHeight);
        }
    }
}