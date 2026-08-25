package org.example.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class RestStation extends PointOfInterest {
    private final int averageStopDuration;

    @JsonCreator
    public RestStation(
            @JsonProperty("name") String name,
            @JsonProperty("country") String country,
            @JsonProperty("x") double x,
            @JsonProperty("y") double y,
            @JsonProperty("averageStopDuration") int averageStopDuration) {
        super(name, country, x, y);
        this.averageStopDuration = averageStopDuration;
    }

    public int getAverageStopDuration() {
        return averageStopDuration;
    }
}