package org.example.gui.components.settings_view_components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import org.example.core.Vehicle;
import org.example.gui.utils.ColorUtils;
import org.example.gui.utils.Initializer;

import java.util.List;

public class GeneralVehicleInformationForm {
    Initializer initializer;
    public GeneralVehicleInformationForm(Initializer initializer) {
        this.initializer = initializer;
    }

    public VBox initializeGeneralVehicleInformation(Vehicle activeVehicle) {
        VBox carInformation = new VBox(15);

        HBox cardHeader = new HBox();
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        Label cardTitle = new Label("ACTIVE VEHICLE");
        cardTitle.getStyleClass().add("card-section-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label engineType = new Label(activeVehicle.getStringEngineType());
        ColorUtils.updateBadgeColor(engineType, activeVehicle.getEngineType(), "badge", List.of("badge-electric", "badge-gasoline", "badge-diesel"));


        Label vehicleName = new Label(activeVehicle.getBrand() + " " + activeVehicle.getModel());
        vehicleName.getStyleClass().add("vehicle-main-name");

        GridPane statsGrid = initializeCarInformationTable(activeVehicle);
        cardHeader.getChildren().addAll(cardTitle, spacer, engineType);
        carInformation.getChildren().addAll(cardHeader, vehicleName, statsGrid);
        return carInformation;
    }

    private GridPane initializeCarInformationTable(Vehicle vehicle) {
        GridPane carInformation = new GridPane();
        carInformation.setHgap(20);
        carInformation.setVgap(20);

        carInformation.add(initializer.createStatItem("YEAR", String.valueOf(vehicle.getReleaseYear()), "stat-label", "stat-value"), 0, 0);
        carInformation.add(initializer.createStatItem("TOTAL KM", vehicle.getTotalKilometres() + " km", "stat-label", "stat-value"), 1, 0);
        carInformation.add(initializer.createStatItem("BATTERY CAPACITY", vehicle.getFuelCapacity() + " kWh", "stat-label", "stat-value"), 2, 0);
        return carInformation;
    }

    /*
    private VBox createStatItem(String infoLabel, String infoValue) {
        VBox box = new VBox(5);
        Label label = new Label(infoLabel);
        label.getStyleClass().add("stat-label");

        Label value = new Label(infoValue);
        value.getStyleClass().add("stat-value");

        box.getChildren().addAll(label, value);
        return box;
    }*/
}
