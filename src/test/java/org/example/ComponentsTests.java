package org.example;

import org.example.algorithms.Dijkstra;
import org.example.algorithms.JourneyState;
import org.example.algorithms.PathResult;
import org.example.calculations.*;
import org.example.core.*;
import org.example.repo.PointOfInterestFileRepository;
import org.example.session.AppSessionTelemetryPreferences;
import org.example.utils.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.time.LocalDateTime;

public class ComponentsTests {
    @TempDir
    Path tempDirVehicles;

    @TempDir
    Path tempDirCities;

    @TempDir
    Path tempDirGasStations;

    @TempDir
    Path tempDirRestaurants;

    @TempDir
    Path tempDirHotels;

    private PointOfInterestService srvPointOfInterest;
    private LocalDateTime startDateTime;

    @BeforeEach
    void setUp() {
        String tempFilePathVehicles = tempDirVehicles.resolve("test_vehicles.json").toString();
        FileUtils.copyTargetTemplate(tempFilePathVehicles, "default_data/default_vehicles.json");

        String tempFilePathCities = tempDirCities.resolve("test_cities.json").toString();
        FileUtils.copyTargetTemplate(tempFilePathCities, "default_data/default_cities.json");

        String tempFilePathGasStations = tempDirGasStations.resolve("test_gas_stations.json").toString();
        FileUtils.copyTargetTemplate(tempFilePathGasStations, "default_data/default_gas_stations.json");

        String tempFilePathHotels = tempDirHotels.resolve("test_hotels.json").toString();
        FileUtils.copyTargetTemplate(tempFilePathHotels, "default_data/default_hotels.json");

        String tempFilePathRestaurants = tempDirRestaurants.resolve("test_restaurants.json").toString();
        FileUtils.copyTargetTemplate(tempFilePathRestaurants, "default_data/default_restaurants.json");

        PointOfInterestFileRepository repoPointOfInterest = new PointOfInterestFileRepository(tempFilePathCities);
        repoPointOfInterest.loadFromFile(tempFilePathGasStations);
        repoPointOfInterest.loadFromFile(tempFilePathHotels);
        repoPointOfInterest.loadFromFile(tempFilePathRestaurants);

        PointOfInterestValidator valPointOfInterest = new PointOfInterestValidator();
        srvPointOfInterest = new PointOfInterestService(repoPointOfInterest, valPointOfInterest);
        startDateTime = LocalDateTime.of(2026, 9, 4, 15, 0);
    }

    @Test
    void testDefaultSpeedAndDrivingTime() {
        AppSessionTelemetryPreferences.getInstance().setCustomMinSpeed(70);
        AppSessionTelemetryPreferences.getInstance().setCustomMaxSpeed(120);
        PointOfInterest Cluj = srvPointOfInterest.findPointOfInterest("Cluj-Napoca", "CITY");
        assertNotNull(Cluj);
        PointOfInterest Budapest = srvPointOfInterest.findPointOfInterest("Budapest", "CITY");
        assertNotNull(Budapest);

        Edge edge1 = new Edge(Cluj, Budapest);
        assertThrows(ServiceException.class, edge1::getSpeedKmh);

        PointOfInterest hotelSky = srvPointOfInterest.findPointOfInterest("Hotel Sky", "HOTEL");
        assertNotNull(hotelSky);

        Edge edge2 = new Edge(Cluj, hotelSky);
        assertThrows(ServiceException.class, edge2::getSpeedKmh);

        PointOfInterest gasStation = srvPointOfInterest.findPointOfInterest("OMV Gilau", "GAS_STATION");
        assertNotNull(gasStation);

        Edge edge3 = new Edge(hotelSky, gasStation);
        double speed = edge3.getSpeedKmh();
        CalculationsService.initializeDefaultSpeed();
        assertTrue(speed >= 70 / 1.25 && speed <= 120 / 0.90);

        Edge edge = new Edge(hotelSky, gasStation);

        double drivingTimeHours = edge.getDrivingTime();
        double distance = edge.getDistanceKm();
        double newSpeed = edge.getSpeedKmh();
        assertEquals(newSpeed, distance / drivingTimeHours, 0.01);
    }

    @Test
    void testRoadAndStopDuration() {
        PointOfInterest hotelSky = srvPointOfInterest.findPointOfInterest("Hotel Sky", "HOTEL");
        PointOfInterest gasStation = srvPointOfInterest.findPointOfInterest("OMV Gilau", "GAS_STATION");
        Edge edge = new Edge(hotelSky, gasStation);

        double drivingTimeHours = edge.getDrivingTime(/*startDateTime*/);

        double stopDurationMinutes = ((RestStation) gasStation).getAverageStopDuration();
        double stopDuration = stopDurationMinutes / 60;

        assertEquals(edge.getRoadAndStopDuration(), stopDuration + drivingTimeHours, 0.01);
    }

    @Test
    void testApplyNodeRefuelAndRestRules() {
        Vehicle vehicle = new Vehicle(1, "Brand1", "Model1", 2010, 100000, Vehicle.EngineType.ICE_DIESEL, 50, 10);

        GasStation gasStation = new GasStation("GasStation1", "Country2", 43.4, 45.65, 30, false, 0.0);
        Hotel hotel = new Hotel("HotelName1", "Country2", 45.45, 60.54, 720, 5);
        Restaurant restaurant = new Restaurant("RestaurantName1", "Country3", 45.54, 32.34, 50, "CousineType1", 8.5);

        JourneyState currentState1 = new JourneyState(gasStation, 100, 3, 10, vehicle.getCurrentFuel());
        JourneyState updatedState1 = Dijkstra.applyNodeRefuelAndRestRules(currentState1, vehicle);

        assertTrue(updatedState1.minutesStopped != 0);
        assertEquals(updatedState1.fuelLiters, vehicle.getFuelCapacity());
        assertEquals(0.0, updatedState1.driveHoursSinceRest);

        JourneyState currentState2 = new JourneyState(gasStation, 100, 3, 10, vehicle.getFuelCapacity() - 1);
        JourneyState updatedState2 = Dijkstra.applyNodeRefuelAndRestRules(currentState2, vehicle);
        assertTrue(updatedState2.minutesStopped != 0);
        assertEquals(0.0, updatedState1.driveHoursSinceRest);
        assertNotEquals(updatedState2.fuelLiters, vehicle.getFuelCapacity(), 0.0);

        JourneyState currentState3 = new JourneyState(gasStation, 100, 2, 10, vehicle.getFuelCapacity());
        JourneyState updatedState3 = Dijkstra.applyNodeRefuelAndRestRules(currentState3, vehicle);
       // assertFalse(updatedState3.minutesStopped != 0);
        assertEquals(0, updatedState3.minutesStopped);

        JourneyState currentState4 = new JourneyState(hotel, 100, 2, 13.5, vehicle.getFuelCapacity());
        JourneyState updatedState4 = Dijkstra.applyNodeRefuelAndRestRules(currentState4, vehicle);
        assertTrue(updatedState4.minutesStopped != 0);

        JourneyState currentState5 = new JourneyState(hotel, 100, 2, 7, vehicle.getFuelCapacity());
        JourneyState updatedState5 = Dijkstra.applyNodeRefuelAndRestRules(currentState5, vehicle);
       // assertFalse(updatedState5.hasStopped);
        assertEquals(0, updatedState5.minutesStopped);

        JourneyState currentState6 = new JourneyState(restaurant, 100, 3, 8, vehicle.getFuelCapacity());
        JourneyState updatedState6 = Dijkstra.applyNodeRefuelAndRestRules(currentState6, vehicle);
        assertTrue(updatedState6.minutesStopped != 0);

        JourneyState currentState7 = new JourneyState(restaurant, 100, 2, 8, vehicle.getFuelCapacity());
        JourneyState updatedState7 = Dijkstra.applyNodeRefuelAndRestRules(currentState7, vehicle);
        //assertFalse(updatedState7.hasStopped);
        assertEquals(0, updatedState7.minutesStopped);

        /*
        //Electric car testing
        Vehicle vehicleElectric = new Vehicle(2, "Brand2", "Model2", 2011, 100000, Vehicle.EngineType.ELECTRIC, 50, 5);

        JourneyState currentState8 = new JourneyState(gasStation, 100, 2, 10, vehicleElectric.getCurrentFuel());
        JourneyState updatedState8 = Dijkstra.applyNodeRefuelAndRestRules(currentState8, vehicleElectric);
        assertFalse(updatedState8.hasStopped);

        JourneyState currentState9 = new JourneyState(gasStation, 100, 3, 10, vehicleElectric.getCurrentFuel());
        JourneyState updatedState9 = Dijkstra.applyNodeRefuelAndRestRules(currentState9, vehicleElectric);
        assertTrue(updatedState9.hasStopped);
        assertNotEquals(updatedState9.fuelLiters, vehicle.getFuelCapacity());
        assertEquals(0.0, updatedState9.driveHoursSinceRest);

        GasStation gasStationElectric = new GasStation("GasStation2", "Country1", 43.4, 45.65, 30, true, 0.0);*/
    }

    @Test
    void testIsValidEdgeTransition() {
        Vehicle vehicle = new Vehicle(1, "Brand1", "Model1", 2010, 100000, Vehicle.EngineType.ICE_DIESEL, 50, 10);

        GasStation gasStation = new GasStation("GasStation1", "Country2", 43.4, 45.65, 30, false, 0.0);
        Hotel hotel = new Hotel("HotelName1", "Country2", 45.45, 60.54, 720, 5);
        Restaurant restaurant = new Restaurant("RestaurantName1", "Country3", 45.54, 32.34, 50, "CousineType1", 8.5);

        JourneyState currentState1 = new JourneyState(gasStation, 100, 3, 10, vehicle.getCurrentFuel());


        JourneyState currentState2 = new JourneyState(gasStation, 100, 3, 10, vehicle.getFuelCapacity() - 1);

        JourneyState currentState3 = new JourneyState(gasStation, 100, 2, 10, vehicle.getFuelCapacity());

        JourneyState currentState4 = new JourneyState(hotel, 100, 2, 13.5, vehicle.getFuelCapacity());

        JourneyState currentState5 = new JourneyState(hotel, 100, 2, 7, vehicle.getFuelCapacity());

        JourneyState currentState6 = new JourneyState(restaurant, 100, 3, 8, vehicle.getFuelCapacity());

        JourneyState currentState7 = new JourneyState(restaurant, 100, 2, 8, vehicle.getFuelCapacity());
      }

    @Test
    void testPathResult() {
        srvPointOfInterest.setMaxConnectDistanceKm(200);
        srvPointOfInterest.buildGraph();
        Graph graph = srvPointOfInterest.getGraph();
        assertNotNull(graph);
        startDateTime = LocalDateTime.of(2026, 9, 4, 8, 0);
        Vehicle vehilce = new Vehicle(1, "Brand1", "Model1", 2010, 100000, Vehicle.EngineType.ICE_DIESEL, 500, 300);

        PathResult result1 = Dijkstra.dijkstra(graph.getAdjacencyList(), srvPointOfInterest.findPointOfInterest("Cluj-Napoca", "CITY"), srvPointOfInterest.findPointOfInterest("Budapest", "CITY"), startDateTime, vehilce);
        assertNotNull(result1);
        assertNotNull(result1.getPointsOfInterest());
        assertFalse(result1.getPointsOfInterest().isEmpty());
        // AlgorithmsTest.printPathDetails(result1, srvPointOfInterest.findPointOfInterest("Cluj-Napoca", "CITY"));

        PathResult result2 = Dijkstra.dijkstra(graph.getAdjacencyList(), srvPointOfInterest.findPointOfInterest("Oradea", "CITY"), srvPointOfInterest.findPointOfInterest("Cluj-Napoca", "CITY"), startDateTime, vehilce);
        assertNotNull(result2);
        assertNotNull(result2.getPointsOfInterest());
        assertFalse(result2.getPointsOfInterest().isEmpty());
        // AlgorithmsTest.printPathDetails(result2, srvPointOfInterest.findPointOfInterest("Oradea", "CITY"));
    }
}