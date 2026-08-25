package org.example.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class City extends PointOfInterest {
    private final double weekdayCongestionFactor;
    private final double weekendCongestionFactor;

    @JsonCreator
    public City(
            @JsonProperty("name") String name,
            @JsonProperty("country") String country,
            @JsonProperty("x") double x,
            @JsonProperty("y") double y,
            @JsonProperty("weekdayCongestionFactor") double weekdayCongestionFactor,
            @JsonProperty("weekendCongestionFactor") double weekendCongestionFactor) {
        super(name, country, x, y);
        this.weekdayCongestionFactor = weekdayCongestionFactor;
        this.weekendCongestionFactor = weekendCongestionFactor;
    }

    public double getWeekdayCongestionFactor() {
        return weekdayCongestionFactor;
    }

    public double getWeekendCongestionFactor() {
        return weekendCongestionFactor;
    }

    public String getStringSearching() {
        return this.getName() + ", " + this.getCountry();
    }

    @Override
    public String getType() {
        return "CITY";
    }


}