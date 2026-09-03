package org.example.gui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.calculations.VehicleService;
import org.example.core.Vehicle;
import org.example.gui.components.settings_view_components.CustomizeVehicleForm;
import org.example.gui.components.settings_view_components.GeneralVehicleInformationForm;
import org.example.gui.components.settings_view_components.TelemetryForm;
import org.example.gui.utils.AppSessionGUI;
import org.example.gui.utils.AutoCompleteHelper;
import org.example.gui.utils.ColorUtils;
import org.example.gui.utils.Initializer;

public class SettingsView extends BorderPane {
    private final Runnable onHomePressed;
    private final Initializer initializer = new Initializer();

    private final VehicleService srvVehicle;

    Vehicle activeVehicle;

    private final VBox dynamicContent = new VBox(20);

    private GeneralVehicleInformationForm generalVehicleInformation;
    private CustomizeVehicleForm customizablePane;
    private TelemetryForm telemetryPane;

    public SettingsView(VehicleService srvVehicle, Runnable onHomePressed) {
        this.srvVehicle = srvVehicle;
        this.onHomePressed = onHomePressed;
        initializer.applyCSS("/style/settings_view.css", this);
        initializeSettingsViewComponents();
    }

    public void refreshUI() {
        this.activeVehicle = AppSessionGUI.getInstance().getActiveVehicle();
        dynamicContent.getChildren().clear();

        if (activeVehicle != null) {
            HBox header = initializeHeader();
            VBox activeVehicleCard = initializeActiveVehicleCard();
            VBox vehicleCustomizeForm = initializeVehicleForm();
      //      VBox telemetryForm = initializeTelemetryForm();

            //old
         //   dynamicContent.getChildren().addAll(header, activeVehicleCard, vehicleCustomizeForm, telemetryForm);

            //new
            dynamicContent.getChildren().addAll(header, activeVehicleCard, vehicleCustomizeForm);
        }
    }

    public void initializeSettingsViewComponents() {
        this.getStyleClass().add("root");

        VBox mainCountainer = new VBox(20);
        mainCountainer.setPadding(new Insets(25));
        mainCountainer.setPrefWidth(380);

        activeVehicle = AppSessionGUI.getInstance().getActiveVehicle();
        generalVehicleInformation = new GeneralVehicleInformationForm(initializer);
        customizablePane = new CustomizeVehicleForm(initializer, srvVehicle, this::refreshUI);
        telemetryPane = new TelemetryForm(initializer, srvVehicle);

        mainCountainer.getChildren().add(dynamicContent);
        refreshUI();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(mainCountainer);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("custom-scroll-pane");

        StackPane centerWrapper = new StackPane(scrollPane);
        centerWrapper.setAlignment(Pos.CENTER);
        this.setCenter(centerWrapper);
    }

    private VBox initializeVehicleForm() {
        VBox vehicleForm = new VBox(20);
        vehicleForm.getStyleClass().add("control-panel");
        vehicleForm.setPadding(new Insets(25));

        HBox formHeader = new HBox();
        formHeader.setAlignment(Pos.CENTER_LEFT);

        Label formTitle = new Label("VEHICLE SELECTION & SETTINGS");
        formTitle.getStyleClass().add("card-section-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        /*
        Button customizeButton = new Button("CUSTOMIZE PARAMETERS");
        ColorUtils.updateCustomizeButtonColor(customizeButton, activeVehicle.getEngineType());
         */

        //old
        //formHeader.getChildren().addAll(formTitle, spacer, customizeButton);

        //new
        formHeader.getChildren().addAll(formTitle, spacer);


        TextField chooseNewVehicle = new TextField();
        AutoCompleteHelper.setupAutoCompleteVehicle(chooseNewVehicle, srvVehicle, selectedVehicle -> {
            AppSessionGUI.getInstance().setActiveVehicle(selectedVehicle);
            refreshUI();
        });

        //old
      //  VBox customizePanel = customizablePane.initializeCustomizablePane(customizeButton, activeVehicle);


        //new
        VBox customizePanel = customizablePane.initializeCustomizablePane(activeVehicle);


        VBox chooseBox = initializer.initializeInputGroup(chooseNewVehicle, "CHOOSE A NEW VEHICLE", "Type brand, model or release year (e. g. Porsche)...");
        vehicleForm.getChildren().addAll(formHeader, chooseBox, customizePanel);
        return vehicleForm;
    }

    private VBox initializeTelemetryForm() {
        VBox telemetryForm = new VBox(15);
        telemetryForm.getStyleClass().add("control-panel");
        telemetryForm.setPadding(new Insets(25));

        Label telemetryTitle = new Label("TELEMETRY & SIMULATION LIMITS");
        telemetryTitle.getStyleClass().add("card-section-title");

        VBox telemetryPanel = telemetryPane.initializeTelemetryPane(activeVehicle);
        //

        telemetryForm.getChildren().addAll(telemetryTitle, telemetryPanel);
        return telemetryForm;
    }

    private HBox initializeHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(15);

        Button homeButton = new Button("HOME");
     //   homeButton.getStyleClass().add("home-button");
        ColorUtils.updateCustomizeButtonColor(homeButton, activeVehicle.getEngineType());

        homeButton.setOnAction(e -> {
            if (onHomePressed != null) {
                onHomePressed.run();
            }
        });

        Label title = new Label("SYSTEM SETTINGS");
        title.getStyleClass().add("navigation-system-title");

        header.getChildren().addAll(homeButton, title);
        return header;
    }

    private VBox initializeActiveVehicleCard() {
        VBox card = new VBox(15);
        card.getStyleClass().add("vehicle-card");
        card.setPadding(new Insets(20));

        VBox generalCarInformation = generalVehicleInformation.initializeGeneralVehicleInformation(activeVehicle);
        VBox fuelStatus = initializer.initializeFuelStatus(activeVehicle, "stat-label");

        card.getChildren().addAll(generalCarInformation, fuelStatus);
        return card;
    }
}