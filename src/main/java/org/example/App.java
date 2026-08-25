package org.example;

import javafx.application.Application;
import org.example.calculations.PointOfInterestService;
import org.example.calculations.VehicleService;
import org.example.core.VehicleValidator;
import org.example.gui.AppGUI;
import org.example.repo.PointOfInterestFileRepository;
import org.example.repo.VehicleFileRepository;

import java.io.File;

public class App
{
    public static void main( String[] args )
    {
        VehicleFileRepository repoVehicle = new VehicleFileRepository("data/vehicles.json");
        VehicleValidator valVehicle = new VehicleValidator();
        VehicleService srvVehicle = new VehicleService(repoVehicle, valVehicle);

        PointOfInterestFileRepository repoPointOfInterest = new PointOfInterestFileRepository("data/cities.json");
        repoPointOfInterest.loadFromFile("data/gas_stations.json");
        repoPointOfInterest.loadFromFile("data/hotels.json");
        repoPointOfInterest.loadFromFile("data/restaurants.json");
        PointOfInterestService srvPointOfInterest = new PointOfInterestService(repoPointOfInterest);

        AppGUI.setServices(srvVehicle, srvPointOfInterest);
        Application.launch(AppGUI.class, args);
    }
}