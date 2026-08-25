package org.example.core;

public class HotelValidator extends RestStationValidator {
    public void validate(Hotel hotel) {
        super.validate(hotel);

        String errors = "";
        if (hotel == null) {
            errors += "Hotel cannot be null\n";
        }

        assert hotel != null;
        if (hotel.getStars() < 0 || hotel.getStars() > 5) {
            errors += "Stars must be between 0 and 5\n";
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}