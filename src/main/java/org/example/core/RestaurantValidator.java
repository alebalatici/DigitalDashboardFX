package org.example.core;

public class RestaurantValidator extends RestStationValidator {
    public void validate(Restaurant restaurant) {
        super.validate(restaurant);

        String errors = "";
        if (restaurant == null) {
            errors = "Restaurant cannot be null\n";
        }

        assert restaurant != null;
        if (restaurant.getRating() < 0 || restaurant.getRating() > 10) {
            errors = "Restaurant rating must be between 0 and 10\n";
        }

        if (restaurant.getCuisineType().isEmpty()) {
            errors = "Cuisine type cannot be empty\n ";
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}