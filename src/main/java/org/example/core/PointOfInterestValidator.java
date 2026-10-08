package org.example.core;

public class PointOfInterestValidator {
    public void validate(PointOfInterest pointOfInterest) {
        String errors = "";
        if (pointOfInterest == null) {
            errors += "PointOfInterest cannot be null\n";
        }

        assert pointOfInterest != null;
        if (pointOfInterest.getName().isEmpty()) {
            errors += "PointOfInterest name cannot be empty\n";
        }

        if (pointOfInterest.getX() < -90 || pointOfInterest.getX() > 90) {
            errors += "The x coordinate must be between -90 and 90\n";
        }

        if (pointOfInterest.getY() < -180 || pointOfInterest.getY() > 180) {
            errors += "The y coordinate must be between -180 and 180\n";
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public void validateMaxConectivityDistance(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }

        try {
            double maxConectivityDistance = Double.parseDouble(text);
            if (maxConectivityDistance < 0) {
                throw new ValidationException("The maximum conectivity distance cannot be negative");
            }
        }

        catch (NumberFormatException e) {
            throw new ValidationException("The maximum conectivity distance must be a valid number");
        }
    }
}
