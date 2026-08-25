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
}
