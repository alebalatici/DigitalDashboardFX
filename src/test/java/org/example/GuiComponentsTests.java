package org.example;

import org.example.core.*;
import org.example.gui.components.simulation_view_components.BoundingBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

public class GuiComponentsTests {
    private City city;
    private GasStation gasStation;
    private Restaurant restaurant;
    private Hotel hotel;

    @BeforeEach
    void setUp() {

        city = new City("CityName1", "Country1", 10.0, 20.0, 1.0, 1.5);
        gasStation = new GasStation("GasStation1", "Country2", 40.0, 5.0, 30, false, 0.0);
        hotel = new Hotel("HotelName1", "Country2", 25.0, 25.0, 720, 5);
        restaurant = new Restaurant("Restaurant1", "Country3", 40.0, 10.0, 50, "CousineType", 9.0);
    }

    @Test
    void testBoundingBoxFrom() {
        List<PointOfInterest> points = List.of(city, gasStation, hotel, restaurant);
        BoundingBox box1 = BoundingBox.from(points);

        assertEquals(10.0, box1.getSouth());
        assertEquals(40.0, box1.getNorth());
        assertEquals(5.0, box1.getWest());
        assertEquals(25.0, box1.getEast());

        BoundingBox box2 = BoundingBox.from(Collections.emptyList());
        assertEquals(Double.MAX_VALUE, box2.getSouth());
        assertEquals(-Double.MAX_VALUE, box2.getNorth());
        assertEquals(Double.MAX_VALUE, box2.getWest());
        assertEquals(-Double.MAX_VALUE, box2.getEast());
    }

    @Test
    void testGetNormalizedCoordinates() {
        List<PointOfInterest> points = List.of(city, gasStation, hotel);
        BoundingBox box = BoundingBox.from(points);
        assertEquals(0.0, box.getNormalizedX(box.getWest()), 0.0001);
        assertEquals(1.0, box.getNormalizedX(box.getEast()), 0.0001);
        assertEquals(0.0, box.getNormalizedY(box.getSouth()), 0.0001);
        assertEquals(1.0, box.getNormalizedY(box.getNorth()), 0.0001);
    }
}