package org.example;

import org.example.algorithms.Dijkstra;
import org.example.algorithms.PathResult;
import org.example.calculations.Graph;
import org.example.calculations.PointOfInterestService;
import org.example.core.Vehicle;
import org.example.repo.PointOfInterestFileRepository;
import org.example.utils.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ComplexComponentsTests {
    @TempDir
    Path tempDirCities;

    @TempDir
    Path tempDirGasStations;

    @TempDir
    Path tempDirRestaurants;

    @TempDir
    Path tempDirHotels;

    private String tempFilePathCities;
    private String tempFilePathGasStations;
    private String tempFilePathRestaurants;
    private String tempFilePathHotels;

    private PointOfInterestFileRepository repoPointOfInterest;
    private PointOfInterestService srvPointOfInterest;
    private LocalDateTime startDateTime;

    @BeforeEach
    void setUp() {
        tempFilePathCities = tempDirCities.resolve("test_cities.json").toString();
        FileUtils.copyTargetTemplate(tempFilePathCities, "complex_data/complex_cities.json");

        tempFilePathGasStations = tempDirGasStations.resolve("test_gas_stations.json").toString();
        FileUtils.copyTargetTemplate(tempFilePathGasStations, "complex_data/complex_gas_stations.json");

        tempFilePathHotels = tempDirHotels.resolve("test_hotels.json").toString();
        FileUtils.copyTargetTemplate(tempFilePathHotels, "complex_data/complex_hotels.json");

        tempFilePathRestaurants = tempDirRestaurants.resolve("test_restaurants.json").toString();
        FileUtils.copyTargetTemplate(tempFilePathRestaurants, "complex_data/complex_restaurants.json");

        repoPointOfInterest = new PointOfInterestFileRepository(tempFilePathCities);
        repoPointOfInterest.loadFromFile(tempFilePathGasStations);
        repoPointOfInterest.loadFromFile(tempFilePathHotels);
        repoPointOfInterest.loadFromFile(tempFilePathRestaurants);

        srvPointOfInterest = new PointOfInterestService(repoPointOfInterest);
        startDateTime = LocalDateTime.of(2026, 9, 4, 15, 0);
    }

    @Test
    void testPathResult() {
        srvPointOfInterest.buildGraph(500);
        Graph graph = srvPointOfInterest.getGraph();
        assertNotNull(graph);
        LocalDateTime startDateTime = LocalDateTime.of(2026, 9, 4, 8, 0);
        Vehicle vehilce = new Vehicle(1, "Brand1", "Model1", 2010, 100000, Vehicle.EngineType.ICE_DIESEL, 75, 75);

        PathResult result1 = Dijkstra.dijkstra(graph.getAdjacencyList(), srvPointOfInterest.findPointOfInterest("Paris", "CITY"), srvPointOfInterest.findPointOfInterest("Budapest", "CITY"), startDateTime, vehilce);
        assertNotNull(result1);
        //AlgorithmsTest.printPathDetails(result1, srvPointOfInterest.findPointOfInterest("Paris", "CITY"));

        PathResult result2 = Dijkstra.dijkstra(graph.getAdjacencyList(), srvPointOfInterest.findPointOfInterest("Istanbul", "CITY"), srvPointOfInterest.findPointOfInterest("Oslo", "CITY"), startDateTime, vehilce);
        assertNotNull(result2);
      //  AlgorithmsTest.printPathDetails(result2, srvPointOfInterest.findPointOfInterest("Istanbul", "CITY"));

        PathResult result3 = Dijkstra.dijkstra(graph.getAdjacencyList(), srvPointOfInterest.findPointOfInterest("Istanbul", "CITY"), srvPointOfInterest.findPointOfInterest("Barcelona", "CITY"), startDateTime, vehilce);
        assertNotNull(result3);
        AlgorithmsTest.printPathDetails(result3, srvPointOfInterest.findPointOfInterest("Istanbul", "CITY"));

        PathResult result4 = Dijkstra.dijkstra(graph.getAdjacencyList(), srvPointOfInterest.findPointOfInterest("Barcelona", "CITY"), srvPointOfInterest.findPointOfInterest("Lisbon", "CITY"), startDateTime, vehilce);
        assertNotNull(result4);
     //   AlgorithmsTest.printPathDetails(result4, srvPointOfInterest.findPointOfInterest("Istanbul", "CITY"));
    }
}