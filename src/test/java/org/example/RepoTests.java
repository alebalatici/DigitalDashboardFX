package org.example;

import org.example.core.*;
import org.example.repo.PointOfInterestFileRepository;
import org.example.repo.RepositoryException;
import org.example.repo.VehicleFileRepository;
import org.example.utils.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class RepoTests {
    @TempDir
    Path tempDirVehicles;

    @TempDir
    Path tempDirCities;

    @TempDir
    Path tempDirGasStations;

    @TempDir
    Path tempDirHotels;

    @TempDir
    Path tempDirRestaurants;

    private String tempFilePathVehicles;
    private String tempFilePathCities;
    private String tempFilePathGasStations;
    private String tempFilePathHotels;
    private String tempFilePathRestaurants;

    @BeforeEach
    void setUp() {
        tempFilePathVehicles = tempDirVehicles.resolve("test_vehicles.json").toString();
        FileUtils.copyTargetTemplate(tempFilePathVehicles, "default_data/default_vehicles.json");

        tempFilePathCities = tempDirCities.resolve("test_cities.json").toString();
        FileUtils.copyTargetTemplate(tempFilePathCities, "default_data/default_cities.json");

        tempFilePathGasStations = tempDirGasStations.resolve("test_gas_stations.json").toString();
        FileUtils.copyTargetTemplate(tempFilePathGasStations, "default_data/default_gas_stations.json");

        tempFilePathHotels = tempDirHotels.resolve("test_hotels.json").toString();
        FileUtils.copyTargetTemplate(tempFilePathHotels, "default_data/default_hotels.json");

        tempFilePathRestaurants = tempDirRestaurants.resolve("test_restaurants.json").toString();
        FileUtils.copyTargetTemplate(tempFilePathRestaurants, "default_data/default_restaurants.json");
    }

    @Test
    void testAddVehicle() {
        VehicleFileRepository repo = new VehicleFileRepository(tempFilePathVehicles);
        assertEquals(10, repo.getAllVehicles().size());
        repo.addVehicle(new Vehicle(11, "brand", "model", 2001, 1000, Vehicle.EngineType.ICE_DIESEL, 700, 500));
        assertEquals(11, repo.getAllVehicles().size());
        assertThrows(RepositoryException.class, () -> repo.addVehicle(new Vehicle(11, "brand", "model", 2001, 1000, Vehicle.EngineType.ICE_DIESEL, 700, 500)));
    }

    @Test
    void testFindVehicle() {
        VehicleFileRepository repo = new VehicleFileRepository(tempFilePathVehicles);
        Vehicle vehicle = repo.findVehicle("Tesla", "Model 3", 2023);
        assertNotNull(vehicle);
        assertEquals(1, vehicle.getId());
        assertEquals("Tesla", vehicle.getBrand());
        assertEquals("Model 3", vehicle.getModel());
        assertEquals(2023, vehicle.getReleaseYear());
        assertEquals(18200, vehicle.getTotalKilometres());
        assertEquals(Vehicle.EngineType.ELECTRIC, vehicle.getEngineType());
        assertEquals(75.0, vehicle.getFuelCapacity());
        assertEquals(60.0, vehicle.getCurrentFuel());
    }

    @Test
    void testAddCity() {
        PointOfInterestFileRepository repo = new PointOfInterestFileRepository(tempFilePathCities);
        assertEquals(10, repo.getAllPointsOfInterest("CITY").size());
        assertEquals(10, repo.getAllPointsOfInterest("ALL").size());
        repo.addPointOfInterest(new City("Name", "Country", 50.0, 51.0, 1.8, 1.4));
        assertEquals(11, repo.getAllPointsOfInterest("CITY").size());
        assertThrows(RepositoryException.class, () -> repo.addPointOfInterest(new City("Name", "Country", 50.0, 51.0, 1.8, 1.4)));
    }

    @Test
    void testFindCity() {
        PointOfInterestFileRepository repo = new PointOfInterestFileRepository(tempFilePathCities);
        City city = (City) repo.findPointOfInterestByNameAndType("Oradea", "CITY");
        assertNotNull(city);
        assertEquals("Oradea", city.getName());
        assertEquals(47.0519, city.getX());
        assertEquals(21.9404, city.getY());
        assertEquals(1.35, city.getWeekdayCongestionFactor());
        assertEquals(1.1, city.getWeekendCongestionFactor());
        assertThrows(RepositoryException.class, () -> repo.findPointOfInterestByNameAndType("Name", "CITY"));
    }

    @Test
    void testAddGasStation() {
        PointOfInterestFileRepository repo = new PointOfInterestFileRepository(tempFilePathGasStations);
        assertEquals(10, repo.getAllPointsOfInterest("GAS_STATION").size());
        assertEquals(10, repo.getAllPointsOfInterest("ALL").size());
        repo.addPointOfInterest(new GasStation("Name", "Country", 50.0, 51.0, 20, false, 0.0));
        assertEquals(11, repo.getAllPointsOfInterest("GAS_STATION").size());
        assertThrows(RepositoryException.class, () -> repo.addPointOfInterest(new GasStation("Name", "Country", 50.0, 51.0, 20, false, 0.0)));
    }

    @Test
    void testFindGasStation() {
        PointOfInterestFileRepository repo = new PointOfInterestFileRepository(tempFilePathGasStations);
        GasStation gasStation = (GasStation) repo.findPointOfInterestByNameAndType("Rompetrol Huedin", "GAS_STATION");
        assertNotNull(gasStation);
        assertEquals("Rompetrol Huedin", gasStation.getName());
        assertEquals(46.8672, gasStation.getX());
        assertEquals(23.0245, gasStation.getY());
        assertEquals(10, gasStation.getAverageStopDuration());
        assertFalse(gasStation.hasHasElectricCharger());
        assertEquals(0.0, gasStation.getChargingPowerKw());
    }

    @Test
    void testAddHotel() {
        PointOfInterestFileRepository repo = new PointOfInterestFileRepository(tempFilePathHotels);
        assertEquals(10, repo.getAllPointsOfInterest("HOTEL").size());
        assertEquals(10, repo.getAllPointsOfInterest("ALL").size());
        repo.addPointOfInterest(new Hotel("Name", "Country",50.0, 51.0, 420, 4));
        assertEquals(11, repo.getAllPointsOfInterest("HOTEL").size());
        assertThrows(RepositoryException.class, () -> repo.addPointOfInterest(new  Hotel("Name", "Country",50.0, 51.0, 420, 4)));
    }

    @Test
    void testFindHotel() {
        PointOfInterestFileRepository repo = new PointOfInterestFileRepository(tempFilePathHotels);
        Hotel hotel =  (Hotel) repo.findPointOfInterestByNameAndType("Hotel Sun", "HOTEL");
        assertNotNull(hotel);
        assertEquals("Hotel Sun", hotel.getName());
        assertEquals(46.7650, hotel.getX());
        assertEquals(23.5510, hotel.getY());
        assertEquals(360, hotel.getAverageStopDuration());
        assertEquals(3, hotel.getStars());
    }

    @Test
    void testAddRestaurant() {
        PointOfInterestFileRepository repo = new PointOfInterestFileRepository(tempFilePathRestaurants);
        assertEquals(10, repo.getAllPointsOfInterest("RESTAURANT").size());
        assertEquals(10, repo.getAllPointsOfInterest("ALL").size());
        repo.addPointOfInterest(new Restaurant("Name", "Country",50.0, 51.0, 50, "Traditional", 4.8));
        assertEquals(11, repo.getAllPointsOfInterest("RESTAURANT").size());
        assertThrows(RepositoryException.class, () -> repo.addPointOfInterest(new  Restaurant("Name", "Country",50.0, 51.0, 50, "Traditional", 4.8)));
    }

    @Test
    void testFindRestaurant() {
        PointOfInterestFileRepository repo = new PointOfInterestFileRepository(tempFilePathRestaurants);
        Restaurant restaurant = (Restaurant) repo.findPointOfInterestByNameAndType("Hanul Conac", "RESTAURANT");
        assertNotNull(restaurant);
        assertEquals("Hanul Conac", restaurant.getName());
        assertEquals(46.7451, restaurant.getX());
        assertEquals(23.4832, restaurant.getY());
        assertEquals(45, restaurant.getAverageStopDuration());
        assertEquals("Traditional", restaurant.getCuisineType());
        assertEquals(4.3, restaurant.getRating());
    }
}