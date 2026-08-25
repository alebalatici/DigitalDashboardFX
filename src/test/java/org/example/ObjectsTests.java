package org.example;

import org.example.core.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ObjectsTests {
    @Test
    void testVehicle() {
        Vehicle vehicle = new Vehicle(1, "brand1", "model1", 2001, 1000, Vehicle.EngineType.ICE_DIESEL, 700, 500);
        assertEquals(1, vehicle.getId());
        assertEquals("brand1", vehicle.getBrand());
        assertEquals("model1", vehicle.getModel());
        assertEquals(2001, vehicle.getReleaseYear());
        assertEquals(1000, vehicle.getTotalKilometres());
        assertEquals(Vehicle.EngineType.ICE_DIESEL, vehicle.getEngineType());
        assertEquals(700, vehicle.getFuelCapacity());
        assertEquals(500, vehicle.getCurrentFuel());
    }

    @Test
    void vehicleValidator() {
        VehicleValidator val = new VehicleValidator();
        Vehicle vehicle1 = new Vehicle(-1, "brand1", "model1", 2001, 1000, Vehicle.EngineType.ICE_DIESEL, 700, 500);
        assertThrows(ValidationException.class, () -> val.validateVehicle(vehicle1));
        Vehicle vehicle2 = new Vehicle(1, "", "model1", 2001, 1000, Vehicle.EngineType.ICE_DIESEL, 700, 500);
        assertThrows(ValidationException.class, () -> val.validateVehicle(vehicle2));
        Vehicle vehicle3 = new Vehicle(1, "brand1", "", 2001, 1000, Vehicle.EngineType.ICE_DIESEL, 700, 500);
        assertThrows(ValidationException.class, () -> val.validateVehicle(vehicle3));
        Vehicle vehicle4 = new Vehicle(1,"brand1", "model1", -2001, 1000, Vehicle.EngineType.ICE_DIESEL, 700, 500);
        assertThrows(ValidationException.class, () -> val.validateVehicle(vehicle4));
        Vehicle vehicle5 = new Vehicle(1, "brand1", "model1", 2001, 200000000, Vehicle.EngineType.ICE_DIESEL, 700, 500);
        assertThrows(ValidationException.class, () -> val.validateVehicle(vehicle5));
        Vehicle vehicle6 = new Vehicle(1, "brand1", "model1", 2001, 1000, Vehicle.EngineType.ICE_DIESEL, -700, 500);
        assertThrows(ValidationException.class, () -> val.validateVehicle(vehicle6));
        Vehicle vehicle7 = new Vehicle(1, "brand1", "model1", 2001, 1000, Vehicle.EngineType.ICE_DIESEL, 700, -500);
        assertThrows(ValidationException.class, () -> val.validateVehicle(vehicle7));
        Vehicle vehicle8 = new Vehicle(1, "brand1", "model1", 2001, 1000, Vehicle.EngineType.ICE_DIESEL, 700, 800);
        assertThrows(ValidationException.class, () -> val.validateVehicle(vehicle8));
    }

    @Test
    void testCity() {
        City city = new City("CityName", "Country", 50, 40, 1.8, 1.2);
        assertEquals("CityName", city.getName());
        assertEquals(50, city.getX());
        assertEquals(40, city.getY());
        assertEquals(1.8, city.getWeekdayCongestionFactor());
        assertEquals(1.2, city.getWeekendCongestionFactor());
    }

    @Test
    void testCityValidator() {
        CityValidator val = new CityValidator();
        City city1 = new City("", "Country", 50, 40, 1.8, 1.2);
        assertThrows(ValidationException.class, () -> val.validate(city1));
        City city2 = new City("CityName", "Country", 150, 40, 1.8, 1.2);
        assertThrows(ValidationException.class, () -> val.validate(city2));
        City city3 = new City("CityName", "Country", 50, -240, 1.8, 1.2);
        assertThrows(ValidationException.class, () -> val.validate(city3));
        City city4 = new City("CityName", "Country", 50, 40, 6, 1.2);
        assertThrows(ValidationException.class, () -> val.validate(city4));
        City city5 = new City("CityName", "Country",50, 40, 1.8, -1);
        assertThrows(ValidationException.class, () -> val.validate(city5));
    }

    @Test
    void testRestaurant() {
        Restaurant restaurant = new Restaurant("RestaurantName", "Country", 50, 40, 120, "CousineType", 9.8);
        assertEquals("RestaurantName", restaurant.getName());
        assertEquals(50, restaurant.getX());
        assertEquals(40, restaurant.getY());
        assertEquals(120, restaurant.getAverageStopDuration());
        assertEquals("CousineType", restaurant.getCuisineType());
        assertEquals(9.8, restaurant.getRating());
    }

    @Test
    void testRestaurantValidator() {
        RestaurantValidator val = new RestaurantValidator();
        Restaurant restaurant1 = new Restaurant("", "Country", 50, 40, 120, "CousineType", 9.8);
        assertThrows(ValidationException.class, () -> val.validate(restaurant1));
        Restaurant restaurant2 = new Restaurant("RestaurantName", "Country",-155, 40, 120, "CousineType", 9.8);
        assertThrows(ValidationException.class, () -> val.validate(restaurant2));
        Restaurant restaurant3 = new Restaurant("RestaurantName", "Country",50, 340, 120, "CousineType", 9.8);
        assertThrows(ValidationException.class, () -> val.validate(restaurant3));
        Restaurant restaurant4 = new Restaurant("RestaurantName", "Country",50, 40, -120, "CousineType", 9.8);
        assertThrows(ValidationException.class, () -> val.validate(restaurant4));
        Restaurant restaurant5 = new Restaurant("RestaurantName", "Country",50, 40, 120, "", 9.8);
        assertThrows(ValidationException.class, () -> val.validate(restaurant5));
        Restaurant restaurant6 = new Restaurant("RestaurantName", "Country",50, 40, 120, "CousineType", 11);
        assertThrows(ValidationException.class, () -> val.validate(restaurant6));
    }

    @Test
    void testHotel() {
        Hotel hotel = new Hotel("HotelName", "Country",50, 40, 720, 4);
        assertEquals("HotelName", hotel.getName());
        assertEquals(50, hotel.getX());
        assertEquals(40, hotel.getY());
        assertEquals(720, hotel.getAverageStopDuration());
        assertEquals(4, hotel.getStars());
    }

    @Test
    void testHotelValidator() {
        HotelValidator val = new HotelValidator();
        Hotel hotel1 = new Hotel("", "Country",50, 40, 720, 4);
        assertThrows(ValidationException.class, () -> val.validate(hotel1));
        Hotel hotel2 = new Hotel("HotelName", "Country", -150, 40, 720, 4);
        assertThrows(ValidationException.class, () -> val.validate(hotel2));
        Hotel hotel3 = new Hotel("HotelName", "Country",50, 1040, 720, 4);
        assertThrows(ValidationException.class, () -> val.validate(hotel3));
        Hotel hotel4 = new Hotel("HotelName", "Country",50, 40, -720, 4);
        assertThrows(ValidationException.class, () -> val.validate(hotel4));
        Hotel hotel5 = new Hotel("HotelName", "Country",50, 40, 720, 10);
        assertThrows(ValidationException.class, () -> val.validate(hotel5));
    }

    @Test
    void testGasStation() {
        GasStation gasStationElectricCharger = new GasStation("GasStation", "Country",50, 40, 20, true, 50.0);
        assertEquals("GasStation", gasStationElectricCharger.getName());
        assertEquals(50, gasStationElectricCharger.getX());
        assertEquals(40, gasStationElectricCharger.getY());
        assertEquals(20, gasStationElectricCharger.getAverageStopDuration());
        assertTrue(gasStationElectricCharger.hasHasElectricCharger());
        assertEquals(50.0, gasStationElectricCharger.getChargingPowerKw());

        GasStation GasStationNoElectricCharger = new GasStation("GasStation", "Country", 50, 40, 20);
        assertEquals("GasStation", GasStationNoElectricCharger.getName());
        assertEquals(50, GasStationNoElectricCharger.getX());
        assertEquals(40, GasStationNoElectricCharger.getY());
        assertEquals(20, GasStationNoElectricCharger.getAverageStopDuration());
        assertFalse(GasStationNoElectricCharger.hasHasElectricCharger());
        assertEquals(0.0, GasStationNoElectricCharger.getChargingPowerKw());
    }

    @Test
    void testGasStationValidator() {
        GasStationValidator val = new GasStationValidator();
        GasStation gasStation1 = new GasStation("", "Country", 50, 40, 20, true, 50.0);
        assertThrows(ValidationException.class, () -> val.validate(gasStation1));
        GasStation gasStation2 = new GasStation("GasStation", "Country", -100, 40, 20, true, 50.0);
        assertThrows(ValidationException.class, () -> val.validate(gasStation2));
        GasStation gasStation3 = new GasStation("GasStation", "Country", 50, -400, 20, true, 50.0);
        assertThrows(ValidationException.class, () -> val.validate(gasStation3));
        GasStation gasStation4 = new GasStation("GasStation", "Country", 50, 40, 20, true, -50.0);
        assertThrows(ValidationException.class, () -> val.validate(gasStation4));
    }
}