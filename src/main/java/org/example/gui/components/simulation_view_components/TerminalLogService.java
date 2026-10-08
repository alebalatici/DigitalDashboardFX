package org.example.gui.components.simulation_view_components;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TerminalLogService
{
    private final VBox terminalLogContainer;
    private final ScrollPane terminalScrollPane;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static final String COLOR_INFO = "#00F0FF";
    public static final String COLOR_SUCCESS = "#00FF66";
    public static final String COLOR_ERROR = "#FF9900";
    public static final String COLOR_DEFAULT = "#FF0055";
    public static final String COLOR_MUTED = "#A0A5B5";

    public TerminalLogService(VBox terminalLogContainer, ScrollPane terminalScrollPane) {
        this.terminalLogContainer = terminalLogContainer;
        this.terminalScrollPane = terminalScrollPane;

        this.terminalLogContainer.setSpacing(2);

        this.terminalLogContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            this.terminalScrollPane.setVvalue(1.0);
        });
    }

    public void log(String message, String colorHex) {
        Platform.runLater(() -> {
            String timeStamp = LocalDateTime.now().format(TIME_FORMATTER);
            Label logLine = new Label(String.format("[%s] %s", timeStamp, message));
            logLine.getStyleClass().add("terminal-log-line");
            logLine.setStyle("-fx-text-fill: " + colorHex + ";");
            logLine.setWrapText(true);
            terminalLogContainer.getChildren().add(logLine);
        });
    }

    public void logSimulatedEvent(LocalDateTime journeyTime, String message, String colorHex) {
        Platform.runLater(() -> {
            String timeStr;
            if (journeyTime != null) {
                timeStr = journeyTime.format(DateTimeFormatter.ofPattern("dd MMM HH:mm"));
            }
            else {
                timeStr = "UNKNOWN";
            }
            Label logLine = new Label(String.format("[%s] %s", timeStr, message));
            logLine.setStyle("-fx-text-fill: " + colorHex + ";");
            logLine.setWrapText(true);
            terminalLogContainer.getChildren().add(logLine);
        });
    }

    public void clear() {
        Platform.runLater(() -> {
            terminalLogContainer.getChildren().clear();
        });
    }
}