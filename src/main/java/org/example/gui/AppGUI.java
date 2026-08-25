package org.example.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.calculations.PointOfInterestService;
import org.example.calculations.VehicleService;
import org.example.gui.utils.Initializer;
import org.example.gui.views.DashboardView;
import org.example.gui.views.WelcomeView;

import java.net.URL;

public class AppGUI extends Application {

    private static VehicleService srvVehicle;
    private static PointOfInterestService srvPointOfInterest;

    Initializer initializer = new Initializer();

    private Stage primaryStage;

    public AppGUI() {

    }

    public static void setServices(VehicleService srvVehicle, PointOfInterestService srvPointOfInterest) {
        AppGUI.srvVehicle = srvVehicle;
        AppGUI.srvPointOfInterest = srvPointOfInterest;
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        primaryStage.setTitle("Travel Infotainment System");

        showWelcomeScreen();

        primaryStage.show();
    }

    private void applyCSS(Scene scene) {
        URL cssResource = getClass().getResource("/style/global.css");
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource.toExternalForm());
        } else {
            System.err.println("[WARNING] /style/global.css nu a fost găsit în classpath (resources)!");
        }
    }

    public void showWelcomeScreen() {
        WelcomeView welcomeView = new WelcomeView(() -> showDashboard());
        Scene scene = new Scene(welcomeView, 1024, 680);

        applyCSS(scene);

        primaryStage.setScene(scene);
    }

    public void showDashboard() {
        DashboardView dashboardView = new DashboardView(srvVehicle, srvPointOfInterest);
        Scene scene = new Scene(dashboardView, 1024, 680);

        applyCSS(scene);

        primaryStage.setScene(scene);
    }
}