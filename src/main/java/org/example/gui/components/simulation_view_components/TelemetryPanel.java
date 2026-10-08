package org.example.gui.components.simulation_view_components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.core.Vehicle;
import org.example.gui.utils.ColorUtils;
import org.example.gui.utils.Initializer;

public class TelemetryPanel {
    private final Vehicle activeVehicle;
    private final Initializer initializer;
    public TelemetryPanel(Vehicle activeVehicle, Initializer initializer) {
        this.activeVehicle = activeVehicle;
        this.initializer = initializer;
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

    public VBox initializeTelemetryPanel() {
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

        telemetryPanel.getChildren().addAll(title, gaugesBox, energyBox);
        return telemetryPanel;
    }
}
