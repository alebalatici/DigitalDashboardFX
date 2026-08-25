package org.example.core;

public class CityValidator extends PointOfInterestValidator {
    public void validate(City city) {
        super.validate(city);

        String errors = "";
        if (city == null) {
            errors += "City must not be null\n";
        }

        assert city != null;
        if (city.getWeekdayCongestionFactor() < 0 || city.getWeekdayCongestionFactor() > 5) {
            errors += "Weekdays Congestion Factor must be between 0 and 5\n";
        }

        if (city.getWeekendCongestionFactor() < 0 || city.getWeekendCongestionFactor() > 5) {
            errors += "Weekends Congestion Factor must be between 0 and 5\n";
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}