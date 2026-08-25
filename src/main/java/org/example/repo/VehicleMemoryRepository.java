package org.example.repo;

import org.example.core.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VehicleMemoryRepository implements VehicleRepository {
    protected final List<Vehicle> vehicles = new ArrayList<>();

    VehicleMemoryRepository() {

    }

    @Override
    public void addVehicle(Vehicle vehicle) {
        boolean exists = vehicles.stream().anyMatch(v -> v.getId() == vehicle.getId());
        if (exists) {
            throw new RepositoryException("A " + vehicle.getId() + " already exists in the system\n");
        }

        vehicles.add(vehicle);
    }

    @Override
    public Vehicle findVehicle(String brand, String model, int year) {
        return vehicles.stream().filter(v -> Objects.equals(v.getBrand(), brand) && Objects.equals(v.getModel(), model) && v.getReleaseYear() == year).findFirst().orElseThrow(() -> new RepositoryException("A " + brand + " " + model + " " + year + " does not exist in the system\n"));
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicles;
    }


}