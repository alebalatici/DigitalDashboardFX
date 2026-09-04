package org.example.session;

import org.example.core.City;
import org.example.core.Vehicle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AppSessionNavigation {
    private static AppSessionNavigation instance;

    private Vehicle activeVehicle;

    private double maxSpeedLimit = 130.0;
    private double maxTemperatureWarning = 90.0;

    private City sourceCity;
    private City destinationCity;
    private LocalDateTime startDateTime = null;

    private LocalDate startDate = null;
    private LocalTime startTime = null;

    private AppSessionNavigation() {}

    public static AppSessionNavigation getInstance() {
        if (instance == null) {
            instance = new AppSessionNavigation();
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

    public LocalDateTime getStartDateTime() {
        if (startDate != null && startTime != null) {
            startDateTime = LocalDateTime.of(startDate, startTime);
        }
        return startDateTime;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }
}