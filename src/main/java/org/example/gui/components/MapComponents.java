package org.example.gui.components;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.example.core.City;
import org.example.core.PointOfInterest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapComponents {
    private double startX;
    private double startY;

    private Group mapGroup;

    private ImageView sourcePin;
    private ImageView destinationPin;

    private final List<Node> activePins = new ArrayList<>();

    private final String mapPath;

    public MapComponents(String mapPath) {
        this.mapPath = mapPath;
    }

    public Pane initializeMapView(double initialPointX, double initialPointY) {
        mapGroup = new Group();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(mapPath)) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;

            if (inputStream != null) {
                doc = dBuilder.parse(inputStream);
            }

            else {
                System.err.println("Failed to load SVG file");
                return new Pane();
            }

            doc.getDocumentElement().normalize();
            NodeList pathList = doc.getElementsByTagName("path");

            for (int i = 0; i < pathList.getLength(); i++) {
                Element element = (Element) pathList.item(i);
                String pathData = element.getAttribute("d");

                if (pathData != null && !pathData.isEmpty()) {
                    SVGPath svgPath = new SVGPath();
                    svgPath.setContent(pathData);

                    svgPath.setFill(Color.web("#FF0055"));
                    svgPath.setStroke(Color.BLACK);
                    svgPath.setStrokeWidth(0.05);

                    mapGroup.getChildren().add(svgPath);
                }
            }

        } catch (Exception e) {
            System.err.println("Failed to load SVG file" + e.getMessage());
        }

        //Europe
        mapGroup.setScaleX(8.0);
        mapGroup.setScaleY(8.0);

        mapGroup.setTranslateX(initialPointX);
        mapGroup.setTranslateY(initialPointY);

        Pane root = new Pane(mapGroup);
        root.setOnScroll(event -> {
            double zoomFactor = 1.1;
            if (event.getDeltaY() < 0) {
                zoomFactor = 1 / zoomFactor;
            }
            mapGroup.setScaleX(mapGroup.getScaleX() * zoomFactor);
            mapGroup.setScaleY(mapGroup.getScaleY() * zoomFactor);
        });

        root.setOnMousePressed(event -> {
            startX = event.getSceneX() - mapGroup.getTranslateX();
            startY = event.getSceneY() - mapGroup.getTranslateY();
        });

        root.setOnMouseDragged(event -> {
            mapGroup.setTranslateX(event.getSceneX() - startX);
            mapGroup.setTranslateY(event.getSceneY() - startY);
        });

/*
        mapGroup.setOnMouseClicked(event -> {
            double clickX = event.getX();
            double clickY = event.getY();

            System.out.println(clickX + " " + clickY);
        });
*/
        return root;
    }

    public ImageView addPinImagePointOfInterest(double x, double y, PointOfInterest pointOfInterest, String pinPath, double widthAndHeight) {
        if (mapGroup == null) {
            return new ImageView();
        }

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(pinPath);
        if (inputStream == null) {
            System.err.println("Failed to load SVG file");
            return new ImageView();
        }

        Image pinImage = new Image(inputStream);
        ImageView pinView = new ImageView(pinImage);

        pinView.setFitWidth(widthAndHeight);
        pinView.setFitHeight(widthAndHeight);
        pinView.setPreserveRatio(true);

        pinView.setX(x - pinView.getFitWidth() / 2);
        pinView.setY(y - pinView.getFitHeight());

        Tooltip tooltip = new Tooltip(pointOfInterest.getName());
        tooltip.setShowDelay(Duration.millis(100));
        tooltip.getStyleClass().add("navigation-tooltip");
        Tooltip.install(pinView, tooltip);

        mapGroup.getChildren().add(pinView);
        activePins.add(pinView);
        return pinView;
    }

    public void setSourcePin(double x, double y, City city, String pinPath, double widthAndHeight) {
        if (sourcePin != null) {
            mapGroup.getChildren().remove(sourcePin);
        }

        sourcePin = addPinImagePointOfInterest(x, y, city, pinPath, widthAndHeight);
    }

    public void setDestinationPin(double x, double y, City city, String pinPath, double widthAndHeight) {
        if (destinationPin != null) {
            mapGroup.getChildren().remove(destinationPin);
        }

        destinationPin = addPinImagePointOfInterest(x, y, city, pinPath, widthAndHeight);
    }

    public void clearActivePins() {
        if (mapGroup != null) {
            mapGroup.getChildren().removeAll(activePins);
        }

        activePins.clear();
    }
}