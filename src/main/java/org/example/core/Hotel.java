package org.example.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Hotel extends RestStation {
    private final int stars;

    @JsonCreator
    public Hotel(
            @JsonProperty("name") String name,
            @JsonProperty("country") String country,
            @JsonProperty("x") double x,
            @JsonProperty("y") double y,
            @JsonProperty("averageStopDuration") int averageStopDuration,
            @JsonProperty("stars") int stars) {
        super(name, country, x, y, averageStopDuration);
        this.stars = stars;
    }

    public int getStars() {
        return stars;
    }

    @Override
    public String getType() {
        return "HOTEL";
    }
}