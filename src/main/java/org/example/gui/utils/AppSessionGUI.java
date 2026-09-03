package org.example.gui.utils;

import org.example.core.City;
import org.example.core.Vehicle;

public class AppSessionGUI {
    private static AppSessionGUI instance;

    private City sourceCity;
    private City destinationCity;

    private Vehicle activeVehicle;

    private double maxSpeedLimit = 130.0;
    private double maxTemperatureWarning = 90.0;

    private AppSessionGUI() {}

    public static AppSessionGUI getInstance() {
        if (instance == null) {
            instance = new AppSessionGUI();
        }
        return instance;
    }

    public City getSourceCity() {
        return sourceCity;
    }

    public void setSourceCity(City sourceCity) {
        this.sourceCity = sourceCity;
    }

    public void setActiveVehicle(Vehicle activeVehicle) {
        this.activeVehicle = activeVehicle;
    }

    public City getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(City destinationCity) {
        this.destinationCity = destinationCity;
    }

    public Vehicle getActiveVehicle() {
        return activeVehicle;
    }

    public void clearRoute() {
        this.destinationCity = null;
        this.sourceCity = null;
    }

    public double getMaxSpeedLimit() {
        return maxSpeedLimit;
    }

    public void setMaxSpeedLimit(double maxSpeedLimit) {
        this.maxSpeedLimit = maxSpeedLimit;
    }

    public double getMaxTemperatureWarning() {
        return maxTemperatureWarning;
    }

    public void setMaxTemperatureWarning(double maxTemperatureWarning) {
        this.maxTemperatureWarning = maxTemperatureWarning;
    }
}