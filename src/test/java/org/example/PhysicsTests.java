package org.example;

import org.example.calculations.CalculationsService;
import org.example.core.City;
import org.example.core.PointOfInterest;
import org.example.core.Restaurant;
import org.example.core.VirtualPoint;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.example.calculations.Physics;

public class PhysicsTests {
    public static final double delta = 0.01;

    @Test
    public void testHarvesineDistance() {
        double result1 = Physics.HavesineDistance(50.2, 70.2, 30.4, 60.5);
        assertEquals(2345.61, result1, delta);
        double result2 = Physics.HavesineDistance(70, 89, 50, 14);
        assertEquals(4340.65, result2, delta);
        double result3 = Physics.HavesineDistance(30, 91, 41.4, 30.8);
        assertEquals(5470.15,  result3, delta);
        assertThrows(IllegalArgumentException.class, () -> Physics.HavesineDistance(120, 89, 50, 14));
        assertThrows(IllegalArgumentException.class, () -> Physics.HavesineDistance(70, 200, 50, 14));
    }

    @Test
    public void testSRVFormat() {
        double[] coordinates = Physics.convertToSvg(46.7712, 23.6236);
        System.out.println(coordinates[0] + " " + coordinates[1]);
    }

    @Test
    public void testVirtualPointCoordinates() {
        double[] coordinates = Physics.virtualPointCoordinates(45, 56.564, 22.54, 67.8, 10);
        double distance = Physics.HavesineDistance(45, 56.564, 22.54, 67.8);
        double distanceFromP1 = Physics.HavesineDistance(coordinates[0], coordinates[1], 45, 56.564);
        double distanceFromP2 = Physics.HavesineDistance(coordinates[0], coordinates[1], 22.54, 67.8);
        assertEquals(distance, distanceFromP1 + distanceFromP2, delta);
        assertEquals(10, distanceFromP1, delta);
        assertEquals(distance - 10, distanceFromP2, delta);
    }

    @Test
    public void testCreateVirtualPointAtDistance() {
        City city = new City("CityName", "CountryName", 40.40, 54.5, 1.5, 1.4);
        Restaurant restaurant = new Restaurant("RestaurantName", "CountryName", 45.54, 34.445, 3, "CousineType", 5.4);
        double distance = CalculationsService.HavesineDistance(city, restaurant);
        VirtualPoint virtualPoint = CalculationsService.createVirtualPointAtDistance(city, restaurant, 10);
        assertNotNull(virtualPoint);
        assertEquals(10, CalculationsService.HavesineDistance(virtualPoint, city), delta);
        assertEquals(distance - 10, CalculationsService.HavesineDistance(virtualPoint, restaurant), delta);
    }
}