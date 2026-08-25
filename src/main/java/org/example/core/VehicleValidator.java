package org.example.core;
import java.time.Year;

public class VehicleValidator {
    public void validateVehicle(Vehicle vehicle) {
        String errors = "";
        if (vehicle == null) {
            errors += "Vehicle cannot be null\n";
        }

        assert vehicle != null;
        if (vehicle.getId() < 0) {
            errors += "Vehicle ID cannot be negative\n";
        }

        if (vehicle.getBrand().isEmpty()) {
            errors += "Brand cannot be empty.\n";
        }

        if (vehicle.getModel().isEmpty()) {
            errors += "Model cannot be empty.\n";
        }

        int currentYear = Year.now().getValue();
        if (vehicle.getReleaseYear() <= 1886 || vehicle.getReleaseYear() > currentYear) {
            errors += "Release year must be between 1886 and " +  currentYear + ".\n";
        }

        if (vehicle.getTotalKilometres() < 0 || vehicle.getTotalKilometres() > 2000000) {
            errors += "Total kilometres must be between 0 and 2000000.\n";
        }

        if (vehicle.getFuelCapacity() < 0) {
            errors += "Fuel capacity cannot be negative.\n";
        }

        if (vehicle.getCurrentFuel() < 0) {
            errors += "Current fuel cannot be negative.\n";
        }

        if (vehicle.getCurrentFuel() > vehicle.getFuelCapacity()) {
            errors += "Current fuel must be less or equal to the vehicle's fuel capacity.\n";
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public void validateFuelCapacity(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }

        try {
            double fuelCapacity = Double.parseDouble(text);
            if (fuelCapacity < 0) {
                throw new ValidationException("Fuel capacity must be a positive number.");
            }
        }
        catch (NumberFormatException e) {
            throw new ValidationException("Fuel capacity must be a valid number.");
        }
    }

    public void validateCurrentFuel(String text, double maxCapacity) {
        if (text == null || text.isEmpty()) {
            return;
        }

        try {
            double currentFuel = Double.parseDouble(text);
            if (currentFuel < 0) {
                throw new ValidationException("Current fuel must be a positive number.");
            }

            if (currentFuel > maxCapacity) {
                throw new ValidationException("Current fuel must be less or equal to the vehicle's fuel capacity.");
            }
        }
        catch (NumberFormatException e) {
            throw new ValidationException("Current fuel must be a valid number.");
        }
    }

    public void validateBaseConsumption(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        try {
            double baseConsumption = Double.parseDouble(text);
            if (baseConsumption <= 0) {
                throw new ValidationException("Base consumption must be a positive number.");
            }
        }
        catch (NumberFormatException e) {
            throw new ValidationException("Base consumption must be a valid number.");
        }
    }

    public void validateMaxSpeed(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }

        try {
            double maxSpeed = Double.parseDouble(text);
            if (maxSpeed <= 0) {
                throw new ValidationException("Max speed must be a positive number");
            }

            if (maxSpeed > 250) {
                throw new ValidationException("Max speed must be less or equal to 250 km.");
            }
        }

        catch (Exception e) {
            throw new ValidationException("Max speed must be a valid number");
        }
    }

    public void validateMaxTemperature(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }

        try {
            double maxTemperature = Double.parseDouble(text);
            if (maxTemperature <= 0) {
                throw new ValidationException("Max temperature must be a positive number");
            }

            if (maxTemperature > 110) {
                throw new ValidationException("Max temperature must be less or equal to 110 C.");
            }
        }

        catch (Exception e) {
            throw new ValidationException("Max temperature must be a valid number");
        }
    }
}