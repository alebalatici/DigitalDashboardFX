package org.example;

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
}