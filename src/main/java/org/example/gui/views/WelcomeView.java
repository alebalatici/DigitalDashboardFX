package org.example.gui.views;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.example.gui.utils.Initializer;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WelcomeView extends StackPane {
    private final Runnable onStartJourney;

    private static MediaPlayer mediaPlayer;

    private final Initializer initializer = new Initializer();

    private Label timeLabel;
    private Label dateLabel;
    private Text titleText;
    private Button startButton;

    public WelcomeView(Runnable onStartJourney) {
        this.onStartJourney = onStartJourney;
        initializeWelcomeViewComponents();
    }

    private void animateText(Text text) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1.5), text);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.08);
        scaleTransition.setToY(1.08);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(Animation.INDEFINITE);
        scaleTransition.play();
    }

    private void initializeWelcomeViewComponents() {
        setupVideoBackground();
        VBox contentBox = new VBox();
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setSpacing(10);

        timeLabel = new Label("00:00:00");
        timeLabel.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace; -fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #FF0055;");

        dateLabel = new Label("SYSTEM INITIALIZATION");
        dateLabel.setStyle("-fx-font-family: 'Segoe UI', monospace; -fx-font-size: 20px; -fx-text-fill: #FF0055;");
        initializer.initializeDateTimeLabels(timeLabel, dateLabel);

        titleText = new Text("WELCOME");
        titleText.getStyleClass().add("title-text");
        animateText(titleText);

        startButton = new Button("MENU");
        startButton.getStyleClass().add("primary-button");
        startButton.setOnAction(event -> onStartJourney.run());

        initializeClock(timeLabel, dateLabel);
        contentBox.getChildren().addAll(titleText, timeLabel, startButton);
        this.getChildren().add(contentBox);
    }

    private void setupVideoBackground() {
        mediaPlayer = initializer.initializeMediaPlayer("/animations/welcome.mp4");
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.fitWidthProperty().bind(widthProperty());
        mediaView.fitHeightProperty().bind(heightProperty());
        mediaView.setPreserveRatio(false);
        this.getChildren().add(mediaView);
    }

    private void initializeClock(Label clockLabel, Label dateLabel) {

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            clockLabel.setText(timeFormatter.format(now));
            dateLabel.setText(dateFormatter.format(now));
        }), new KeyFrame(Duration.seconds(1)));

        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }
}