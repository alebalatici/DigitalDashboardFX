package org.example.gui.utils;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.example.core.Vehicle;

import java.util.List;

public class ColorUtils {
    /**
     * Updates a label's style depending on a engine tyle
     * @param badgeLabel The label
     * @param engineType The vehicle's engine type
     * @param generalStyleClass The general style class
     * @param inheritedStyleClasses The list of classes (0 - ELECTRIC, 1 - GASOLINE, 2 - DIESEL)
     */
    public static void updateBadgeColor(Label badgeLabel, Vehicle.EngineType engineType, String generalStyleClass, List<String> inheritedStyleClasses) {
        nodeSetup(badgeLabel, generalStyleClass, inheritedStyleClasses);
        switch (engineType) {
            case ELECTRIC -> badgeLabel.getStyleClass().add(inheritedStyleClasses.get(0));

            case ICE_GASOLINE -> badgeLabel.getStyleClass().add(inheritedStyleClasses.get(1));

            case ICE_DIESEL -> badgeLabel.getStyleClass().add(inheritedStyleClasses.get(2));
        }
    }

    public static void updateScrollBarColor(ProgressBar progressBar, Vehicle.EngineType engineType) {
        nodeSetup(progressBar, "neon-progress-bar", List.of("progress-electric", "progress-gasoline", "progress-diesel"));
        switch (engineType) {
            case ELECTRIC -> progressBar.getStyleClass().add("progress-electric");

            case ICE_GASOLINE -> progressBar.getStyleClass().add("progress-gasoline");

            case ICE_DIESEL -> progressBar.getStyleClass().add("progress-diesel");
        }
    }

    public static void updateProgressBarColor(ProgressBar progressBar, Vehicle.EngineType engineType) {
        nodeSetup(progressBar, "neon-progress-bar", List.of("neon-progress-bar-electric", "neon-progress-bar-gasoline", "neon-progress-bar-diesel"));
        switch (engineType) {
            case ELECTRIC -> progressBar.getStyleClass().add("neon-progress-bar-electric");

            case ICE_GASOLINE -> progressBar.getStyleClass().add("neon-progress-bar-gasoline");

            case ICE_DIESEL -> progressBar.getStyleClass().add("neon-progress-bar-diesel");
        }
    }

    public static void updateCustomizeButtonColor(Button customizeButton, Vehicle.EngineType engineType) {
        nodeSetup(customizeButton, "customize-button", List.of("customize-button-electric", "customize-button-gasoline", "customize-button-diesel"));
        switch (engineType) {
            case ELECTRIC -> customizeButton.getStyleClass().add("customize-button-electric");

            case ICE_GASOLINE -> customizeButton.getStyleClass().add("customize-button-gasoline");

            case  ICE_DIESEL -> customizeButton.getStyleClass().add("customize-button-diesel");
        }
    }

    private static void nodeSetup(Node node, String generalClass, List<String> inheritedClasses) {
        for (String inheritedClass : inheritedClasses) {
            node.getStyleClass().remove(inheritedClass);
        }
        if (!node.getStyleClass().contains(generalClass)) {
            node.getStyleClass().add(generalClass);
        }
    }
}