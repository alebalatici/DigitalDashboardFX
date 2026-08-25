package org.example.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Vehicle {
    public enum EngineType { ICE_GASOLINE, ICE_DIESEL, ELECTRIC }

    private final int id;
    private final String brand;
    private final String model;
    private final int releaseYear;
    private int totalKilometres;
    private final EngineType engineType;

    private double fuelCapacity;
    private double currentFuel;
    private double baseConsumption; //base consumption for 100 km

    private double currentSpeed;
    private double currentRpm;
    private double engineTemperature;

    private double batteryHealth = 100;

    @JsonCreator
    public Vehicle(
            @JsonProperty("id") int id,
            @JsonProperty("brand") String brand,
            @JsonProperty("model") String model,
            @JsonProperty("releaseYear") int releaseYear,
            @JsonProperty("totalKilometres") int totalKilometres,
            @JsonProperty("engineType") EngineType engineType,
            @JsonProperty("fuelCapacity") double fuelCapacity,
            @JsonProperty("currentFuel") double currentFuel) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.releaseYear = releaseYear;
        this.totalKilometres = totalKilometres;
        this.engineType = engineType;
        this.fuelCapacity = fuelCapacity;
        this.currentFuel = currentFuel;
        this.currentSpeed = 0.0;
        if (engineType == EngineType.ELECTRIC) {
            this.currentRpm = 0.0;
        }
        else {
            this.currentRpm = 800;
        }
        this.engineTemperature = 20.0;
    }

    public Vehicle(Vehicle ot) {
        this.id = ot.id;
        this.brand = ot.brand;
        this.model = ot.model;
        this.releaseYear = ot.releaseYear;
        this.totalKilometres = ot.totalKilometres;
        this.engineType = ot.engineType;
        this.fuelCapacity = ot.fuelCapacity;
        this.currentFuel = ot.currentFuel;
        this.currentSpeed = ot.currentSpeed;
        this.currentRpm = ot.currentRpm;
        this.engineTemperature = ot.engineTemperature;
    }

    public int getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public int getTotalKilometres() {
        return totalKilometres;
    }

    public void setTotalKilometres(int totalKilometres) {
        this.totalKilometres = totalKilometres;
    }


    public EngineType getEngineType() {
        return engineType;
    }


    public double getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(double fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }


    public double getCurrentFuel() {
        return currentFuel;
    }

    public void setCurrentFuel(double currentFuel) {
        this.currentFuel = currentFuel;
    }


    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(double speed) {
        this.currentSpeed = speed;
    }


    public double getCurrentRpm() {
        return currentRpm;
    }

    public void setCurrentRpm(double rpm) {
        this.currentRpm = rpm;
    }


    public double getEngineTemperature() {
        return engineTemperature;
    }

    public void setEngineTemperature(double temperature) {
        this.engineTemperature = temperature;
    }


    public double getBaseConsumption() {
        return  baseConsumption;
    }

    public void setBaseConsumption(double baseConsumption) {
        this.baseConsumption = baseConsumption;
    }


    public double getBatteryHealth() {
        return batteryHealth;
    }

    public void setBatteryHealth(double batteryHealth) {
        this.batteryHealth = batteryHealth;
    }


    public String getStringSearching() {
        return brand + " " + model + " " + releaseYear;
    }

    public String getStringEngineType() {
        if (engineType == EngineType.ELECTRIC) {
            return "ELECTRIC";
        }

        else if (engineType == EngineType.ICE_GASOLINE) {
            return "GASOLINE";
        }

        else if (engineType == EngineType.ICE_DIESEL) {
            return "DIESEL";
        }

        else return "UNKNOWN";
    }
}