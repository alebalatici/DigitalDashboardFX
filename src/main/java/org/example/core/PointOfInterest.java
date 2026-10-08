package org.example.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointOfInterest that = (PointOfInterest) o;
        if (Double.compare(that.x, x) != 0) return false;
        if (Double.compare(that.y, y) != 0) return false;
        if (!Objects.equals(name, that.name)) return false;
        return Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, country, x, y);
    }
}