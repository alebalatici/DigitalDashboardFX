package org.example.gui.utils;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.example.core.Vehicle;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class Initializer {
    public URL findResource(String path) {
        URL resource = getClass().getResource(path);
        if (resource == null) {
            resource = Thread.currentThread().getContextClassLoader().getResource("/animations/welcome.mp4");
        }

        if (resource == null) {
            System.err.println("Failed to load resource: " + path);
            return null;
        }
        return resource;
    }

    public void initializeDateTimeLabels(Label timeLabel, Label dateLabel) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            timeLabel.setText(timeFormatter.format(now));
            dateLabel.setText(dateFormatter.format(now));
        }), new KeyFrame(Duration.seconds(1)));

        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    public MediaPlayer initializeMediaPlayer(String path) {
        URL resource = findResource(path);
        Media media = new Media(resource.toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
        return mediaPlayer;
    }

    public VBox initializeInputGroup(TextField textField, String labelText, String placeholder) {
        VBox inputGroup = new VBox();
        inputGroup.setSpacing(8);

        Label label = new Label(labelText);
        label.getStyleClass().add("input-label-text");

        textField.setPromptText(placeholder);
        textField.setPrefHeight(40);

        textField.getStyleClass().add("input-text-field");
        inputGroup.getChildren().addAll(label, textField);
        return inputGroup;
    }

    public VBox initializeSliderGroup(Slider slider, String labelText, double minValue, double maxValue, double currentValue) {
        VBox group = new VBox();
        group.setSpacing(10);
        group.setPadding(new Insets(15));

        Label label = new Label(labelText);
        label.getStyleClass().add("edit-group-label");

        slider.setMin(minValue);
        slider.setMax(maxValue);
        slider.setValue(currentValue);

        group.getChildren().addAll(label, slider);
        return group;
    }

    public VBox initializeEditGroup(TextField textField, String labelText, String placeholder, Consumer<String> validatorLogic) {
        VBox group = new VBox();

        HBox editGroup = new HBox();
        editGroup.setSpacing(15);
        editGroup.setAlignment(Pos.CENTER_LEFT);
        editGroup.getStyleClass().add("custom-edit-group");

        Label label = new Label(labelText);
        label.getStyleClass().add("edit-group-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        textField.setPromptText(placeholder);
        textField.setPrefHeight(40);
        textField.getStyleClass().add("edit-group-field");

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("edit-group-error");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        VBox inputWrapper = new VBox(3);
        inputWrapper.setAlignment(Pos.CENTER_RIGHT);
        inputWrapper.getChildren().addAll(textField, errorLabel);

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                validatorLogic.accept(newValue);

                errorLabel.setVisible(false);
                errorLabel.setManaged(false);
                textField.getStyleClass().remove("input-error");
            }
            catch (Exception e) {
                errorLabel.setText(e.getMessage());
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);

                if (!textField.getStyleClass().contains("input-error")) {
                    textField.getStyleClass().add("input-error");
                }
            }
        });

        editGroup.getChildren().addAll(label, spacer, inputWrapper);
        group.getChildren().addAll(editGroup);
        return group;
    }

    public void initializeMessageLabel(Label messageLabel) {
        messageLabel.setManaged(false);
        messageLabel.setVisible(false);
    }

    public void showMessageLabel(Label messageLabel, String message, String styleClass) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().add(styleClass);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }

    public VBox initializeFuelStatus(Vehicle activeVehicle, String labelStyleClass) {
        VBox fuelStatus = new VBox(5);
        String fuelLabelTitle;
        if (activeVehicle.getEngineType() == Vehicle.EngineType.ICE_DIESEL || activeVehicle.getEngineType() == Vehicle.EngineType.ICE_GASOLINE) {
            fuelLabelTitle = "CURRENT FUEL LEVEL";
        }
        else {
            fuelLabelTitle = "CURRENT CHARGE LEVEL";
        }

        double percentage = (activeVehicle.getCurrentFuel() / activeVehicle.getFuelCapacity()) * 100;
        Label fuelLabel = new Label(fuelLabelTitle + " (" + (int) Math.floor(percentage) + "%)");
        fuelLabel.getStyleClass().add(labelStyleClass);

        ProgressBar fuelBar = new ProgressBar(percentage / 100.0);
        fuelBar.setMaxWidth(Double.MAX_VALUE);
        fuelBar.getStyleClass().add("neon-progress-bar");
        ColorUtils.updateProgressBarColor(fuelBar, activeVehicle.getEngineType());

        fuelStatus.getChildren().addAll(fuelLabel, fuelBar);
        return fuelStatus;
    }

    public void applyCSS(String cssResourcePath, Parent targetNode) {
        URL cssResource = getClass().getResource(cssResourcePath);
        if (cssResource != null) {
            targetNode.getStylesheets().add(cssResource.toExternalForm());
        }
        else {
            System.err.println("Failed to load resource: " + cssResourcePath);
        }
    }

    public VBox createStatItem(String infoLabel, String infoValue, String labelStyleClass, String valueStyleClass, List<String> inheritedStyleClasses, Vehicle activeVehicle) {
        VBox box = new VBox(5);
        Label label = new Label(infoLabel);
        label.getStyleClass().add(labelStyleClass);

        Label value = new Label(infoValue);
        value.getStyleClass().add(valueStyleClass);

        if (activeVehicle != null) {
            ColorUtils.updateBadgeColor(value, activeVehicle.getEngineType(), valueStyleClass, inheritedStyleClasses);
        }

        box.getChildren().addAll(label, value);
        return box;
    }

    public VBox createStatItem(String infoLabel, String infoValue, String labelStyleClass, String valueStyleClass) {
        return createStatItem(infoLabel, infoValue, labelStyleClass, valueStyleClass, null, null);
    }

    public List<String> getIngeritedClasses(String generalClass) {
        return List.of(generalClass + "-electric", generalClass + "-gasoline", generalClass + "-diesel");
    }
}