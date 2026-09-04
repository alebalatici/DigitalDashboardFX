package org.example.gui.utils;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.calculations.PointOfInterestService;
import org.example.calculations.VehicleService;
import org.example.core.City;
import org.example.core.Vehicle;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.function.Consumer;

import java.util.List;

public class AutoCompleteHelper {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static void setupAutoCompleteCity(TextField textField, PointOfInterestService srvPointOfInterest, Consumer<City> selectedCity) {
        ContextMenu popup = new ContextMenu();
        popup.setAutoHide(true);

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                popup.hide();
                return;
            }

            List<City> matches = srvPointOfInterest.getAllCitiesWithString(newValue, srvPointOfInterest.getSortedCities(srvPointOfInterest.getOnlyCities()));
            if (!matches.isEmpty()) {
                popup.getItems().clear();

                for (City city : matches) {
                    Label label = new Label(city.getStringSearching());
                    label.getStyleClass().add("auto-complete-label");
                    CustomMenuItem menuItem = new CustomMenuItem(label, true);

                    menuItem.setOnAction(event -> {
                        textField.setText(city.getStringSearching());
                        textField.positionCaret(city.getStringSearching().length());
                        popup.hide();

                        if (selectedCity != null) {
                            selectedCity.accept(city);
                        }
                    });

                    popup.getItems().add(menuItem);
                }

                if (!popup.isShowing()) {
                    popup.show(textField, Side.BOTTOM, 0, 0);
                }
                else {
                    popup.hide();
                }
            }
        });
    }

    public static void setupAutoCompleteVehicle(TextField textField, VehicleService srvVehicle, Consumer<Vehicle> selectedVehicle) {
        ContextMenu popup = new ContextMenu();
        popup.setAutoHide(true);

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                popup.hide();
                return;
            }

            List<Vehicle> matches = srvVehicle.getAllVehiclesWithString(newValue, srvVehicle.getSortedVehicles(srvVehicle.getAllVehicles()));
            if (!matches.isEmpty()) {
                popup.getItems().clear();

                for (Vehicle vehicle : matches) {
                    Label label = new Label(vehicle.getStringSearching());
                    label.getStyleClass().add("auto-complete-label");
                    CustomMenuItem menuItem = new CustomMenuItem(label, true);
                    menuItem.setOnAction(event -> {
                        textField.setText(vehicle.getStringSearching());
                        textField.positionCaret(vehicle.getStringSearching().length());
                        popup.hide();
                        if (selectedVehicle != null) {
                            selectedVehicle.accept(vehicle);
                        }
                    });

                    popup.getItems().add(menuItem);
                }

                if (!popup.isShowing()) {
                    popup.show(textField, Side.BOTTOM, 0, 0);
                }
                else {
                    popup.hide();
                }
            }
        });
    }

    public static void setupAutoCompleteTime(TextField textField, Consumer<LocalTime> onTimeSelected) {
        ContextMenu popup = new ContextMenu();
        popup.setAutoHide(true);

        List<String> timeSuggestions = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < 60; m += 15) {
                timeSuggestions.add(String.format("%02d:%02d", h, m));
            }
        }

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                popup.hide();
                return;
            }

            String query = newValue.trim();
            List<String> matches = timeSuggestions.stream().filter(time -> time.startsWith(query)).limit(5).toList();

            if (!matches.isEmpty()) {
                popup.getItems().clear();

                for (String timeStr : matches) {
                    Label label = new Label(timeStr);
                    label.getStyleClass().add("auto-complete-label");

                    CustomMenuItem menuItem = new CustomMenuItem(label, true);
                    menuItem.setOnAction(event -> {
                        textField.setText(timeStr);
                        textField.positionCaret(timeStr.length());
                        popup.hide();

                        if (onTimeSelected != null) {
                            LocalTime time = LocalTime.parse(timeStr, TIME_FORMATTER);
                            onTimeSelected.accept(time);
                        }
                    });

                    popup.getItems().add(menuItem);
                }

                if (!popup.isShowing()) {
                    popup.show(textField, Side.BOTTOM, 0, 0);
                }
            }

            else {
                popup.hide();
            }
        });

        textField.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (!isFocused) {
                popup.hide();
            }
        });
    }
}
