package org.example.repo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.core.Vehicle;

import java.io.File;
import java.util.List;

public class VehicleFileRepository extends VehicleMemoryRepository {
    private final String fileName;
    private final ObjectMapper mapper;

    public VehicleFileRepository(String fileName) {
        super();
        this.fileName = fileName;
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);

        loadFromFile();
    }

    private void loadFromFile() {
        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }

        try {
            List<Vehicle> loadedVehicles = mapper.readValue(file, new TypeReference<>() {});
            for (Vehicle vehicle : loadedVehicles) {
                super.addVehicle(vehicle);
            }
        }

        catch (Exception e) {
            throw new RepositoryException("Failed to load vehicles from JSON file: " + e.getMessage());
        }
    }

    public void saveToFile() {
        try {
            File file = new File(fileName);
            //the parent folders exist
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            mapper.writeValue(file, getAllVehicles());
        }
        catch (Exception e) {
            throw new RepositoryException("Failed to save vehicles to JSON file: " + e.getMessage());
        }
    }

    @Override
    public void addVehicle(Vehicle vehicle) {
        super.addVehicle(vehicle);
        saveToFile();
    }

    @Override
    public Vehicle findVehicle(String brand, String model, int year) {
        return super.findVehicle(brand, model, year);
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return super.getAllVehicles();
    }
}