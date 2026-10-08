package org.example.gui.components.simulation_view_components;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.calculations.PointOfInterestService;
import org.example.core.Vehicle;
import org.example.gui.utils.ColorUtils;
import org.example.gui.utils.Initializer;
import org.example.utils.StringUtils;

public class ControlsPanel {
    private final Initializer initializer;
    private final PointOfInterestService srvPointOfInterest;
    private final Vehicle activeVehicle;
    private final Runnable onSettingsPressed;

    public ControlsPanel(Initializer initializer, PointOfInterestService srvPointOfInterest, Vehicle activeVehicle, Runnable onSettingsPressed) {
        this.initializer = initializer;
        this.srvPointOfInterest = srvPointOfInterest;
        this.activeVehicle = activeVehicle;
        this.onSettingsPressed = onSettingsPressed;
    }

    public VBox initializeGraphMaxDistanceEditGroup() {
        VBox editGroup = new VBox(15);

        Label subtitle = new Label("GRAPH CONECTIVITY RANGE");
        subtitle.getStyleClass().addAll("card-section-title");

        TextField textFieldGraphConectivity = new TextField();
        VBox editGraphConectivity = initializer.initializeEditGroup(textFieldGraphConectivity, "GRAPH CONECTIVITY DISTANCE", "Current: " + srvPointOfInterest.getMaxConnectDistanceKm(), srvPointOfInterest::validateMaxConnectDistanceKm);

        VBox saveBox = new VBox(10);
        saveBox.setPadding(new Insets(15));

        Button saveButton = new Button("SAVE CHANGES");
        ColorUtils.updateCustomizeButtonColor(saveButton, activeVehicle.getEngineType());

        Label verificationLabel = new Label();
        initializer.initializeMessageLabel(verificationLabel);

        saveBox.getChildren().addAll(verificationLabel, saveButton);

        saveButton.setOnMouseClicked(event -> {
            try {
                srvPointOfInterest.validateMaxConnectDistanceKm(textFieldGraphConectivity.getText());

                double maxConectivityKm = StringUtils.parseDoubleOrDefault(textFieldGraphConectivity.getText(), srvPointOfInterest.getMaxConnectDistanceKm());

                srvPointOfInterest.rebuildGraph(maxConectivityKm);
                initializer.showMessageLabel(verificationLabel, "SAVED", "succesfull-verification");
            }

            catch (Exception exception) {
                initializer.showMessageLabel(verificationLabel, "SAVE FAILED: INVALID DATA", "error-verification");
            }
        });

        editGroup.getChildren().addAll(subtitle, editGraphConectivity, saveBox);
        return editGroup;
    }

    public HBox initializeCarSettingsEditGroup() {
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
        vehicleSettingsBox.getChildren().addAll(carTitle, spacer, changeVehicleButton);
        return vehicleSettingsBox;
    }
}
