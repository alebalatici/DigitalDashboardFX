package org.example.calculations;

import org.example.core.Vehicle;
import org.example.core.VehicleValidator;
import org.example.repo.VehicleRepository;
import org.example.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class VehicleService {
    private final VehicleRepository repo;
    private final VehicleValidator val;

    public VehicleService(VehicleRepository repo, VehicleValidator val) {
        this.repo = repo;
        this.val = val;
    }

    public void addVehicle(int id, String brand, String model, int releaseYear, int totalKilometres, Vehicle.EngineType engineType, double fuelCapacity, double currentFuel) {
        try {
            Vehicle vehicle = new Vehicle(id, brand, model, releaseYear, totalKilometres, engineType, fuelCapacity, currentFuel);
            val.validateVehicle(vehicle);
            repo.addVehicle(vehicle);
        }

        catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public Vehicle findVehicle(String brand, String model, int year) {
        try {
            return repo.findVehicle(brand, model, year);
        }

        catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public List<Vehicle> getAllVehicles() {
        return repo.getAllVehicles();
    }

    public Vehicle getRandomVehicle() {
        if (repo.getAllVehicles().isEmpty()) {
            return null;
        }

        int randomIndex = ThreadLocalRandom.current().nextInt(repo.getAllVehicles().size());
        return repo.getAllVehicles().get(randomIndex);
    }

    public List<Vehicle> getAllVehiclesWithString(String string, List<Vehicle> listOfVehicles) {
        if (string == null || string.isEmpty()) {
            return new ArrayList<>(listOfVehicles);
        }

        String searchLower = StringUtils.removeDiacritics(string.toLowerCase());
        String[] keywords = searchLower.split("[,\\s]+");
        return listOfVehicles.stream().filter(v -> {
            String brand = StringUtils.removeDiacritics(v.getBrand().toLowerCase());
            String model = StringUtils.removeDiacritics(v.getModel().toLowerCase());
            String releaseYear = String.valueOf(v.getReleaseYear());
            return Arrays.stream(keywords).allMatch(keyword -> brand.contains(keyword) || model.contains(keyword) || releaseYear.contains(keyword));
        }).limit(5).toList();
    }

    public List<Vehicle> getSortedVehicles(List<Vehicle> listOfVehicles) {
        if (listOfVehicles.isEmpty()) {
            return new ArrayList<>(listOfVehicles);
        }

        return listOfVehicles.stream().sorted(Comparator.comparing(Vehicle::getBrand, String.CASE_INSENSITIVE_ORDER).thenComparing(Vehicle::getModel, String.CASE_INSENSITIVE_ORDER).thenComparing(Comparator.comparing(Vehicle::getReleaseYear).reversed())).toList();
    }

    public void modifyVehicleParameters(Vehicle vehicle, double newFuelCapacity, double newCurrentFuel, double newBaseConsumption) {
        try {
            Vehicle testVehicle = new Vehicle(vehicle);
            testVehicle.setFuelCapacity(newFuelCapacity);
            testVehicle.setCurrentFuel(newCurrentFuel);
            testVehicle.setBaseConsumption(newBaseConsumption);
            VehicleValidator val = new VehicleValidator();
            val.validateVehicle(testVehicle);

            vehicle.setFuelCapacity(newFuelCapacity);
            vehicle.setCurrentFuel(newCurrentFuel);
            vehicle.setBaseConsumption(newBaseConsumption);
        }

        catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public void validateFuelCapacity(String text) {
        val.validateFuelCapacity(text);
    }

    public void validateCurrentFuel(String text) {
        val.validateFuelCapacity(text);
    }

    public void validateBaseConsumption(String text) {
        val.validateBaseConsumption(text);
    }

    public void validateMaxSpeed(String text) {
        val.validateMaxSpeed(text);
    }

    public void validateMaxTemperature(String text) {
        val.validateMaxTemperature(text);
    }
}