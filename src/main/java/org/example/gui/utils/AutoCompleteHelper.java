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

import java.util.function.Consumer;

import java.util.List;

public class AutoCompleteHelper {
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
}
