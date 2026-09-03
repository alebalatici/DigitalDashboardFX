package org.example.gui.components.settings_view_components;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.example.calculations.VehicleService;
import org.example.core.Vehicle;
import org.example.gui.utils.ColorUtils;
import org.example.gui.utils.Initializer;
import org.example.utils.StringUtils;

public class CustomizeVehicleForm {
    private final Initializer initializer;
    private final VehicleService srvVehicle;

    private final Runnable onRefreshRequired;

    public CustomizeVehicleForm(Initializer initializer, VehicleService srvVehicle, Runnable onRefreshRequired) {
        this.initializer = initializer;
        this.srvVehicle = srvVehicle;
        this.onRefreshRequired = onRefreshRequired;
    }

    public VBox initializeCustomizablePane(Vehicle activeVehicle) {
        VBox customizePanel = createAdvancedCustomizePanel(activeVehicle);

        /*
        customizePanel.setManaged(false);
        customizePanel.setVisible(false);

        customizeButton.setOnAction(e -> {
            boolean isVisible = customizePanel.isVisible();
            customizePanel.setVisible(!isVisible);
            customizePanel.setManaged(!isVisible);

            if (!isVisible) {
                customizeButton.setText("CLOSE CUSTOMIZATION");
                FadeTransition fadeTransition = new FadeTransition(Duration.millis(250), customizePanel);
                fadeTransition.setFromValue(0.0);
                fadeTransition.setToValue(1.0);
                fadeTransition.play();
            } else {
                customizeButton.setText("CUSTOMIZE PARAMETERS");
            }
        });*/
        return customizePanel;
    }

    private VBox createAdvancedCustomizePanel(Vehicle activeVehicle) {
        VBox customPanel = new VBox(15);
        customPanel.setPadding(new Insets(15));
        customPanel.getStyleClass().add("custom-panel");

        /*
        Label subtitle = new Label("ADVANCED DYNAMICS & OVERRIDES");
        subtitle.getStyleClass().addAll("card-section-title");
         */

        VBox firstEditGroup = initializeFirstEditGroup(activeVehicle);
        VBox secondEditGroup = initializeSecondEditGroup(activeVehicle);

        customPanel.getChildren().addAll(firstEditGroup, secondEditGroup);
        return customPanel;
    }

    private VBox initializeFirstEditGroup(Vehicle activeVehicle) {
        VBox editGroup = new VBox(15);

        Label subtitle = new Label("ENERGY & CONSUMPTION DYNAMICS");
        subtitle.getStyleClass().addAll("card-section-title");

        TextField textFieldFuelCapacity = new TextField();
        VBox editFuelCapacity = initializer.initializeEditGroup(textFieldFuelCapacity, "FUEL CAPACITY", "Current: " + activeVehicle.getFuelCapacity(), srvVehicle::validateFuelCapacity);

        TextField textFieldCurrentFuel = new TextField();
        VBox editCurrentFuel = initializer.initializeEditGroup(textFieldCurrentFuel, "CURRENT FUEL", "Current: " + activeVehicle.getCurrentFuel(), srvVehicle::validateCurrentFuel);

        TextField textFieldBaseConsumption = new TextField();
        VBox editBaseConsumption = initializer.initializeEditGroup(textFieldBaseConsumption, "BASE CONSUMPTION", "Current: " + activeVehicle.getBaseConsumption(), srvVehicle::validateBaseConsumption);

        Button saveFirstEditGroup = new Button("SAVE CHANGES");
        ColorUtils.updateCustomizeButtonColor(saveFirstEditGroup, activeVehicle.getEngineType());

        HBox saveBox = new HBox(10);
        saveBox.setPadding(new Insets(15));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label verificationLabel = new Label();

        initializer.initializeMessageLabel(verificationLabel);

        saveBox.getChildren().addAll(verificationLabel, spacer, saveFirstEditGroup);

        saveFirstEditGroup.setOnMouseClicked(e -> {
            try {
                srvVehicle.validateFuelCapacity(textFieldFuelCapacity.getText());
                srvVehicle.validateCurrentFuel(textFieldCurrentFuel.getText());
                srvVehicle.validateBaseConsumption(textFieldBaseConsumption.getText());

                double fuelCapacity = StringUtils.parseDoubleOrDefault(textFieldFuelCapacity.getText(), activeVehicle.getFuelCapacity());
                double currentFuelCapacity = StringUtils.parseDoubleOrDefault(textFieldCurrentFuel.getText(), activeVehicle.getCurrentFuel());
                double baseConsumption = StringUtils.parseDoubleOrDefault(textFieldBaseConsumption.getText(), activeVehicle.getBaseConsumption());

                srvVehicle.modifyVehicleParameters(activeVehicle, fuelCapacity, currentFuelCapacity, baseConsumption);
                initializer.showMessageLabel(verificationLabel, "SAVED", "succesfull-verification");

                if (onRefreshRequired != null) {
                    onRefreshRequired.run();
                }
            }

            catch (Exception exception) {
                initializer.showMessageLabel(verificationLabel, "SAVE FAILED: INVALID DATA", "error-verification");
            }
        });

        editGroup.getChildren().addAll(subtitle, editFuelCapacity, editCurrentFuel, editBaseConsumption, saveBox);

        return editGroup;
    }

    private VBox initializeBatteryHealthSliderGroup(Vehicle activeVehicle, Slider sliderBatteryHealth) {
        VBox editSliderBatteryHealth = initializer.initializeSliderGroup(sliderBatteryHealth, "BATTERY HEALTH" , 0, 100, activeVehicle.getBatteryHealth());

        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");

        Runnable updateBatteryHealth;

        updateBatteryHealth = () -> {
            setupSliderAndLabelBatteryHealth(sliderBatteryHealth, statusLabel);
        };

        updateBatteryHealth.run();
        sliderBatteryHealth.valueProperty().addListener((observable, oldVal, newVal) -> {
            updateBatteryHealth.run();
        });

        javafx.application.Platform.runLater(updateBatteryHealth);

        VBox batteryHealthBox = new VBox(10);
        batteryHealthBox.getChildren().addAll(editSliderBatteryHealth, statusLabel);
        return batteryHealthBox;
    }

    private VBox initializeTotalKilometersGroup(Vehicle activeVehicle, Slider sliderTotalKilometers) {
        VBox editSliderTotalKilometers = initializer.initializeSliderGroup(sliderTotalKilometers, "TOTAL KILOMETERS", 0, 500000, activeVehicle.getTotalKilometres());

        Label statusLabelTotalKilometers = new Label();
        statusLabelTotalKilometers.getStyleClass().add("status-label");

        Runnable updateTotalKilometers;

        updateTotalKilometers = () -> {
            setupSliderAndLabelTotalKilometers(sliderTotalKilometers, statusLabelTotalKilometers);
        };

        updateTotalKilometers.run();
        sliderTotalKilometers.valueProperty().addListener((observable, oldVal, newVal) -> {
            updateTotalKilometers.run();
        });

        javafx.application.Platform.runLater(updateTotalKilometers);

        VBox totalKilometersBox = new VBox(10);
        totalKilometersBox.getChildren().addAll(editSliderTotalKilometers, statusLabelTotalKilometers);
        return totalKilometersBox;
    }

    private VBox initializeSecondEditGroup(Vehicle activeVehicle) {
        VBox editGroup = new VBox(15);
        editGroup.setSpacing(20);

        Label subtitle = new Label("VEHICLE WEAR & CONDITION");
        subtitle.getStyleClass().add("card-section-title");

        Slider sliderBatteryHealth = new Slider();
        Slider sliderTotalKilometers = new Slider();

        VBox batteryHealthBox = initializeBatteryHealthSliderGroup(activeVehicle, sliderBatteryHealth);
        VBox totalKilometersBox = initializeTotalKilometersGroup(activeVehicle, sliderTotalKilometers);

        HBox saveChangesBox = new HBox(10);
        saveChangesBox.setPadding(new Insets(15));

        Label saveLabel = new Label();
        initializer.initializeMessageLabel(saveLabel);

        Button saveSecondEditGroup = new Button("SAVE CHANGES");
        ColorUtils.updateCustomizeButtonColor(saveSecondEditGroup, activeVehicle.getEngineType());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        saveChangesBox.getChildren().addAll(spacer, saveLabel, saveSecondEditGroup);

        saveSecondEditGroup.setOnMouseClicked(e -> {
           double valBatteryHealth = sliderBatteryHealth.getValue();
           int valTotalKilometers = (int) sliderTotalKilometers.getValue();

           activeVehicle.setBatteryHealth(valBatteryHealth);
           activeVehicle.setTotalKilometres(valTotalKilometers);
           initializer.showMessageLabel(saveLabel, "SAVED", "succesfull-verification");

           if (onRefreshRequired != null) {
               onRefreshRequired.run();
           }
        });

        editGroup.getChildren().addAll(subtitle, batteryHealthBox, totalKilometersBox, saveChangesBox);
        return editGroup;
    }

    private void setupSliderAndLabelBatteryHealth(Slider sliderBatteryHealth, Label statusLabel) {
        double val = sliderBatteryHealth.getValue();
        double pct = (val - sliderBatteryHealth.getMin()) / (sliderBatteryHealth.getMax() - sliderBatteryHealth.getMin());

        String statusText;
        String styleClass;
        String colorHex;

        if (val >= 90) {
            statusText = "BATTERY HEALTH: " + (int) Math.floor(val) + "% - EXCELLENT";
            styleClass = "status-excellent";
            colorHex = "#00FFCC";
        }

        else if (val >= 80) {
            statusText = "BATTERY HEALTH: " + (int) Math.floor(val) + "% - GOOD";
            styleClass = "status-good";
            colorHex = "#FFE600";
        }

        else if (val >= 70) {
            statusText = "BATTERY HAEALTH: " + (int) Math.floor(val) + "% - DEGRADED";
            styleClass = "status-degraded";
            colorHex = "#BF55EC";
        }

        else {
            statusText = "BATTERY HAEALTH: " + (int) Math.floor(val) + "% - CRITICAL";
            styleClass = "status-critical";
            colorHex = "#FF0000";
        }

        statusLabel.setText(statusText);
        statusLabel.getStyleClass().removeAll("status-excellent", "status-good", "status-degraded", "status-critical");
        statusLabel.getStyleClass().add(styleClass);

        String trackGradient = String.format("-fx-background-color: linear-gradient(to right, %s 0%%, %s %.2f%%, #1a1a2e %.2f%%, #1a1a2e 100%%);",
                colorHex, colorHex, pct * 100, pct * 100);

        Node trackNode = sliderBatteryHealth.lookup(".track");
        if (trackNode != null) {
            trackNode.setStyle(trackGradient);
        }
    }

    private void setupSliderAndLabelTotalKilometers(Slider sliderTotalKilometers, Label statusLabel) {
        double val = sliderTotalKilometers.getValue();
        double pct = (val - sliderTotalKilometers.getMin()) / (sliderTotalKilometers.getMax() - sliderTotalKilometers.getMin());

        String statusText;
        String styleClass;
        String colorHex;

        if (val <= 5000) {
            statusText = "MILEAGE: " + (int) Math.floor(val) + " KM - SHOWROOM / ZERO WEAR";
            styleClass = "status-pristine";
            colorHex = "#2ECC71";
        }

        else if (val <= 30000) {
            statusText = "MILEAGE: " + (int) Math.floor(val) + " KM - LOW MILEAGE";
            styleClass = "status-low";
            colorHex = "#00F0FF";
        }

        else if (val <= 80000) {
            statusText = "MILEAGE: " + (int) Math.floor(val) + " KM - NORMAL";
            styleClass = "status-good";
            colorHex = "#FFE600";
        }

        else if (val <= 150000) {
            statusText = "MILEAGE: " + (int) Math.floor(val) + " KM - MODERATE WEAR";
            styleClass = "status-moderate";
            colorHex = "#FFA100";
        }

        else if (val <= 250000) {
            statusText = "MILEAGE: " + (int) Math.floor(val) + " KM - HIGH MILEAGE";
            styleClass = "status-degraded";
            colorHex = "#BF55EC";
        }

        else {
            statusText = "MILEAGE: " + (int) Math.floor(val) + " KM - SEVERE WEAR";
            styleClass = "status-critical";
            colorHex = "#FF0000";
        }

        statusLabel.setText(statusText);
        statusLabel.getStyleClass().removeAll(
                "status-pristine", "status-low", "status-good", "status-moderate", "status-degraded", "status-critical"
        );
        statusLabel.getStyleClass().add(styleClass);

        String trackGradient = String.format("-fx-background-color: linear-gradient(to right, %s 0%%, %s %.2f%%, #1a1a2e %.2f%%, #1a1a2e 100%%);",
                colorHex, colorHex, pct * 100, pct * 100);

        Node trackNode = sliderTotalKilometers.lookup(".track");
        if (trackNode != null) {
            trackNode.setStyle(trackGradient);
        }
    }
}