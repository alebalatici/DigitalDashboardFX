package org.example.core;

public class RestStationValidator extends PointOfInterestValidator {
    public void validate(RestStation restStation) {
        super.validate(restStation);
        String errors = "";
        if (restStation == null) {
            errors += "RestStation cannot be null\n";
        }

        assert restStation != null;
        if (restStation.getAverageStopDuration() < 0) {
            errors += "Average Stop Duration cannot be negative\n";
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
