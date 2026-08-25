package org.example.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Restaurant extends RestStation {
    private final String cuisineType;
    private final double rating;

    @JsonCreator
    public Restaurant(
            @JsonProperty("name") String name,
            @JsonProperty("country") String country,
            @JsonProperty("x") double x,
            @JsonProperty("y") double y,
            @JsonProperty("averageStopDuration") int averageStopDuration,
            @JsonProperty("cousineType") String cousineType,
            @JsonProperty("rating") double rating) {
        super(name, country, x, y, averageStopDuration);
        this.cuisineType = cousineType;
        this.rating = rating;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public double getRating() {
        return rating;
    }

    @Override
    public String getType() {
        return "RESTAURANT";
    }
}