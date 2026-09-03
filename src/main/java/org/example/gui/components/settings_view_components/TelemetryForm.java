package org.example.gui.components.settings_view_components;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.calculations.VehicleService;
import org.example.core.Vehicle;
import org.example.gui.utils.AppSessionGUI;
import org.example.gui.utils.ColorUtils;
import org.example.gui.utils.Initializer;
import org.example.utils.StringUtils;

public class TelemetryForm {
    private final Initializer initializer;
    private final VehicleService srvVehicle;

    public TelemetryForm(Initializer initializer, VehicleService srvVehicle) {
        this.initializer = initializer;
        this.srvVehicle = srvVehicle;
    }

    public VBox initializeTelemetryPane(Vehicle activeVehicle) {
        VBox telemetryPanel = new VBox(15);
        telemetryPanel.setPadding(new Insets(15));
        telemetryPanel.getStyleClass().add("custom-panel");

        VBox editGroup = new VBox(15);

        TextField textFieldMaxSpeed = new TextField();
        int currentSpeedLimit = (int) AppSessionGUI.getInstance().getMaxSpeedLimit();
        VBox editMaxSpeed = initializer.initializeEditGroup(textFieldMaxSpeed, "SPEED LIMIT", "Current: " + currentSpeedLimit, srvVehicle::validateMaxSpeed);

        TextField textFieldMaxTemperature = new TextField();
        int currentMaxTemperature = (int) AppSessionGUI.getInstance().getMaxTemperatureWarning();
        VBox editMaxTemperature = initializer.initializeEditGroup(textFieldMaxTemperature, "TEMPERATURE LIMIT", "Current: " + currentMaxTemperature, srvVehicle::validateMaxTemperature);

        Button saveTelemetrySettingsButton = new Button("SAVE CHANGES");
        ColorUtils.updateCustomizeButtonColor(saveTelemetrySettingsButton, activeVehicle.getEngineType());

        HBox saveBox = new HBox(10);
        saveBox.setPadding(new Insets(15));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label verificationLabel = new Label();
        initializer.initializeMessageLabel(verificationLabel);

        saveBox.getChildren().addAll(verificationLabel, spacer, saveTelemetrySettingsButton);

        saveTelemetrySettingsButton.setOnAction(e -> {
            try {
                srvVehicle.validateMaxSpeed(textFieldMaxSpeed.getText());
                srvVehicle.validateMaxTemperature(textFieldMaxTemperature.getText());

                double maxSpeedLimit = StringUtils.parseDoubleOrDefault(textFieldMaxSpeed.getText(), currentSpeedLimit);
                double maxTemperatureLimit = StringUtils.parseDoubleOrDefault(textFieldMaxTemperature.getText(), currentMaxTemperature);

                AppSessionGUI.getInstance().setMaxSpeedLimit(maxSpeedLimit);
                AppSessionGUI.getInstance().setMaxTemperatureWarning(maxTemperatureLimit);
                initializer.showMessageLabel(verificationLabel, "SAVED", "succesfull-verification");
            }

            catch (Exception exception) {
                initializer.showMessageLabel(verificationLabel, "SAVE FAILED: INVALID DATA", "error-verification");
            }
        });

        editGroup.getChildren().addAll(editMaxSpeed, editMaxTemperature, saveBox);
        telemetryPanel.getChildren().add(editGroup);
        return telemetryPanel;
    }
}
