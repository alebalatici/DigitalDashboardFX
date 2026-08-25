package org.example.repo;

import org.example.core.Vehicle;
import java.util.List;

public interface VehicleRepository {
    void addVehicle(Vehicle vehicle);
    Vehicle findVehicle(String brand, String model, int year);
    List<Vehicle> getAllVehicles();
}