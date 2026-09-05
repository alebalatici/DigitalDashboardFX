package org.example;

import org.example.core.*;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;

public class AlgorithmsTest {
    private City city1;
    private City city2;
    private City city3;
    private City city4;
    private City city5;

    private GasStation gasStation1;
    private GasStation gasStation2;

    private Restaurant restaurant1;

    private Hotel hotel1;
    private Hotel hotel2;
    private Hotel hotel3;

    private LocalDateTime startDateTime;

    @BeforeEach
    void setUp() {
        city1 = new City("CityName1", "Country1", 43.65, 78.87, 1.0, 1.5);
        city2 = new City("CityName2", "Country1", 56.65, 23.65, 1.2, 0.95);
        city3 = new City("CityName3", "Country2", 67.67, 29.54, 1.0, 1.3);
        city4 = new City("CityName4", "Country3", 89.67, 23.34, 1.23, 0.23);
        city5 = new City("CityName5", "Country2", 89.43, 32.54, 1.2, 0.8);

        gasStation1 = new GasStation("GasStation1", "Country2", 43.4, 45.65, 30, false, 0.0);
        gasStation2 = new GasStation("GasStation2", "Country3", 43.45, 54.34, 20, false, 0.0);

        hotel1 = new Hotel("HotelName1", "Country2", 45.45, 60.54, 720, 5);
        hotel2 = new Hotel("HotelName2", "Country1", 23.34, 32.23, 700, 4);

        startDateTime = LocalDateTime.of(2026, 9, 4, 8, 0);
    }
}
