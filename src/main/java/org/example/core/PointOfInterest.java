package org.example.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = City.class, name = "CITY"),
        @JsonSubTypes.Type(value = GasStation.class, name = "GAS_STATION"),
        @JsonSubTypes.Type(value = Restaurant.class, name = "RESTAURANT"),
        @JsonSubTypes.Type(value = Hotel.class, name = "HOTEL")
})

public abstract class PointOfInterest {
    private final String name;
    private final String country;
    private final double x;
    private final double y;

    public PointOfInterest(
            @JsonProperty("name") String name,
            @JsonProperty("country") String country,
            @JsonProperty("x") double x,
            @JsonProperty("y") double y) {
        this.name = name;
        this.country = country;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public String getCountry() { return country; }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public abstract String getType();
}