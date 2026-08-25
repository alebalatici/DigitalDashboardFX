package org.example;

import org.example.calculations.PointOfInterestService;
import org.example.calculations.ServiceException;
import org.example.calculations.VehicleService;
import org.example.core.City;
import org.example.core.Vehicle;
import org.example.core.VehicleValidator;
import org.example.repo.PointOfInterestFileRepository;
import org.example.repo.VehicleFileRepository;
import org.example.repo.VehicleRepository;
import org.example.utils.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class ServiceTests {
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
        VehicleValidator val = new VehicleValidator();
        VehicleService srv = new VehicleService(repo, val);
        assertEquals(10, srv.getAllVehicles().size());
        srv.addVehicle(11, "brand", "model", 2001, 1000, Vehicle.EngineType.ICE_DIESEL, 700, 500);
        assertEquals(11, srv.getAllVehicles().size());
        assertThrows(ServiceException.class, () -> srv.addVehicle(11, "brand", "model", 2001, 1000, Vehicle.EngineType.ICE_DIESEL, 700, 500));
    }

    @Test
    void testAddCity() {
        PointOfInterestFileRepository repo = new PointOfInterestFileRepository(tempFilePathCities);
        PointOfInterestService srv = new PointOfInterestService(repo);
        assertEquals(10, srv.getAllPointsOfInterest("CITY").size());
        srv.addCity("Name", "Country", 50.0, 51.0, 1.8, 1.4);
        assertEquals(11, srv.getAllPointsOfInterest("CITY").size());
        assertThrows(ServiceException.class, () -> srv.addCity("Name", "Country", 50.0, 51.0, 1.8, 1.4));
        assertThrows(ServiceException.class, () -> srv.addCity("", "Country", 50.0, 51.0, 1.8, 1.4));
    }

    @Test
    void testAddGasStation() {
        PointOfInterestFileRepository repo = new PointOfInterestFileRepository(tempFilePathGasStations);
        PointOfInterestService srv = new PointOfInterestService(repo);
        assertEquals(10, srv.getAllPointsOfInterest("GAS_STATION").size());
        srv.addGasStation("Name", "Country", 50.0, 51.0, 20, false, 0.0);
        assertEquals(11, repo.getAllPointsOfInterest("GAS_STATION").size());
        assertThrows(ServiceException.class, () -> srv.addGasStation("Name", "Country", 50.0, 51.0, 20, false, 0.0));
        assertThrows(ServiceException.class, () -> srv.addGasStation("Name", "Country", 91.00, 51.0, 20, false, 0.0));
    }

    @Test
    void testAddHotel() {
        PointOfInterestFileRepository repo = new PointOfInterestFileRepository(tempFilePathHotels);
        PointOfInterestService srv = new PointOfInterestService(repo);
        assertEquals(10, srv.getAllPointsOfInterest("HOTEL").size());
        srv.addHotel("Name", "Country", 50.0, 51.0, 420, 4);
        assertEquals(11, repo.getAllPointsOfInterest("HOTEL").size());
        assertThrows(ServiceException.class, () -> srv.addHotel("Name", "Country", 50.0, 51.0, 420, 4));
        assertThrows(ServiceException.class, () -> srv.addHotel("Name", "Country", 50.0, 181.0, 420, 4));
    }

    @Test
    void testAddRestaurant() {
        PointOfInterestFileRepository repo = new PointOfInterestFileRepository(tempFilePathRestaurants);
        PointOfInterestService srv = new PointOfInterestService(repo);
        assertEquals(10, srv.getAllPointsOfInterest("RESTAURANT").size());
        srv.addRestaurant("Name", "Country", 50.0, 51.0, 50, "Traditional", 4.8);
        assertEquals(11, repo.getAllPointsOfInterest("RESTAURANT").size());
        assertThrows(ServiceException.class, () -> srv.addRestaurant("Name", "Country", 50.0, 51.0, 50, "Traditional", 4.8));
    }

    @Test
    void testGetOnlyCities() {
        PointOfInterestFileRepository repo = new PointOfInterestFileRepository(tempFilePathCities);
        PointOfInterestService srv = new PointOfInterestService(repo);
        assertEquals(10, srv.getOnlyCities().size());
    }

    @Test
    void testSearchCity() {
        PointOfInterestFileRepository repo = new PointOfInterestFileRepository(tempFilePathCities);
        PointOfInterestService srv = new PointOfInterestService(repo);
        assertEquals(1, srv.getAllCitiesWithString("Cluj", srv.getSortedCities(srv.getOnlyCities())).size());
        assertEquals(5, srv.getAllCitiesWithString("RO", srv.getSortedCities(srv.getOnlyCities())).size());
        assertEquals(1, srv.getAllCitiesWithString("Oradea", srv.getSortedCities(srv.getOnlyCities())).size());
        assertEquals(1, srv.getAllCitiesWithString("Budapest", srv.getSortedCities(srv.getOnlyCities())).size());
        assertEquals(1, srv.getAllCitiesWithString("Debrecen", srv.getSortedCities(srv.getOnlyCities())).size());
        assertEquals(1, srv.getAllCitiesWithString("Cl", srv.getSortedCities(srv.getOnlyCities())).size());
    }

    @Test
    void testSearchVehicle() {
        VehicleRepository repo = new VehicleFileRepository(tempFilePathVehicles);
        VehicleValidator val = new VehicleValidator();
        VehicleService srv = new VehicleService(repo, val);
        assertEquals(1, srv.getAllVehiclesWithString("tes", srv.getSortedVehicles(srv.getAllVehicles())).size());
        assertEquals(1, srv.getAllVehiclesWithString("Taycan", srv.getAllVehicles()).size());
        assertEquals(1, srv.getAllVehiclesWithString("2020", srv.getAllVehicles()).size());
        assertEquals(1, srv.getAllVehiclesWithString("ford", srv.getAllVehicles()).size());
        assertEquals(4, srv.getAllVehiclesWithString("4", srv.getAllVehicles()).size());
    }

    @Test
    void testModifyVehicleParameters() {
        VehicleRepository repo = new VehicleFileRepository(tempFilePathVehicles);
        VehicleValidator val = new VehicleValidator();
        VehicleService srv = new VehicleService(repo, val);
        Vehicle vehicle = srv.findVehicle("Tesla", "Model 3", 2023);
        srv.modifyVehicleParameters(vehicle, 500, 100, 10);
        assertEquals(500, vehicle.getFuelCapacity());
        assertEquals(100, vehicle.getCurrentFuel());
        assertEquals(10, vehicle.getBaseConsumption());
        assertThrows(ServiceException.class, () -> srv.modifyVehicleParameters(vehicle, 100, 500, 10));
    }
}