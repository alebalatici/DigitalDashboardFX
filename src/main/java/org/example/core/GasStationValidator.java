package org.example.core;

public class GasStationValidator extends RestStationValidator {
    public void validate(GasStation gasStation) {
        super.validate(gasStation);

        String errors = "";
        if (gasStation == null) {
            errors += "GasStation cannot be null\n";
        }

        assert gasStation != null;
        if (gasStation.getChargingPowerKw() < 0) {
            errors += "ChargingPowerKw cannot be less or equal to 0\n";
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}