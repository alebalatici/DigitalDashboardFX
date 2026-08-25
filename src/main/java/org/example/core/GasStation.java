package org.example.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GasStation extends RestStation {
    private final boolean hasElectricCharger;
    private final double chargingPowerKw;

    @JsonCreator
    public GasStation(
            @JsonProperty("name") String name,
            @JsonProperty("country") String country,
            @JsonProperty("x") double x,
            @JsonProperty("y") double y,
            @JsonProperty("averageStopDuration") int averageStopDuration,
            @JsonProperty("hasElectricCharger") boolean hasElectricCharger,
            @JsonProperty("chargingPowerKw") double chargingPowerKw) {
        super(name, country, x, y, averageStopDuration);
        this.hasElectricCharger = hasElectricCharger;
        this.chargingPowerKw = chargingPowerKw;
    }

    public GasStation(String name, String country, double x, double y, int averageStopDuration) {
        this(name, country, x, y, averageStopDuration, false, 0.0);
    }

    public boolean hasHasElectricCharger() {
        return hasElectricCharger;
    }

    public double getChargingPowerKw() {
        return chargingPowerKw;
    }

    @Override
    public String getType() {
        return "GAS_STATION";
    }
}