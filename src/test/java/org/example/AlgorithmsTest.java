package org.example;

import org.example.algorithms.Dijkstra;
import org.example.algorithms.PathResult;
import org.example.calculations.Edge;
import org.example.calculations.Graph;
import org.example.calculations.PointOfInterestService;
import org.example.core.*;
import org.example.repo.PointOfInterestMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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

    private PointOfInterestMemoryRepository repo;
    private PointOfInterestService srv;

    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        /*
        city1 = new City("CityName1", "Country1", 43.65, 78.87, 1.0, 1.5);
        city2 = new City("CityName2", "Country1", 56.65, 23.65, 1.2, 0.95);
        city3 = new City("CityName3", "Country2", 67.67, 29.54, 1.0, 1.3);
        city4 = new City("CityName4", "Country3", 89.67, 23.34, 1.23, 0.23);
        city5 = new City("CityName5", "Country2", 89.43, 32.54, 1.2, 0.8);

        gasStation1 = new GasStation("GasStation1", "Country2", 43.4, 45.65, 30, false, 0.0);
        gasStation2 = new GasStation("GasStation2", "Country3", 43.45, 54.34, 20, false, 0.0);

        hotel1 = new Hotel("HotelName1", "Country2", 45.45, 60.54, 720, 5);
        hotel2 = new Hotel("HotelName2", "Country1", 23.34, 32.23, 700, 4);

        vehicle = new Vehicle(1, "Brand1", "Model1", 2010, 100000, Vehicle.EngineType.ICE_DIESEL, 500, 300);

        startDateTime = LocalDateTime.of(2026, 9, 4, 8, 0);
        repo = new PointOfInterestMemoryRepository();
        repo.addPointOfInterest(city1);
        repo.addPointOfInterest(city2);
        repo.addPointOfInterest(city3);
        repo.addPointOfInterest(city4);
        repo.addPointOfInterest(city5);
        repo.addPointOfInterest(gasStation1);
        repo.addPointOfInterest(gasStation2);
        repo.addPointOfInterest(hotel1);
        repo.addPointOfInterest(hotel2);
        */


        srv = new PointOfInterestService(repo);
        srv.buildGraph(1000);
    }

    /*
    For debugging
     */
    public static void printPathDetails(PathResult result, PointOfInterest start) {
        if (result == null) {
            System.out.println("\n❌ Nu s-a găsit niciun traseu valid!");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        System.out.println("\n=================== DETALII TRASEU ===================");
        System.out.println("Distanta totala: " + result.getTotalKm() + " km");
        System.out.println("Timp total: " + String.format("%.1f", result.getTotalHours()) + " minute");
        System.out.println("Cost total (ponderi): " + result.getTotalCost());
        System.out.println("------------------------------------------------------");

        Map<PointOfInterest, LocalDateTime> arrivalTimes = result.getArrivalTimes();

        System.out.println("📍 START: " + start.getName() + " [" + start.getClass().getSimpleName() + "]");
        System.out.println("   Plecarea la: " + arrivalTimes.get(start).format(formatter));
        System.out.println("   |");

        int step = 1;
        for (Edge edge : result.getPath()) {
            PointOfInterest destination = edge.getDestination();
            LocalDateTime arrival = arrivalTimes.get(destination);

            System.out.println("   +--- (" + edge.getDistanceKm() + " km) --->");
            System.out.println("📍 PASUL " + step + ": " + destination.getName() + " [" + destination.getClass().getSimpleName() + "]");
            System.out.println("   Ora sosirii: " + (arrival != null ? arrival.format(formatter) : "N/A"));
            System.out.println("   Coordonate: " + destination.getX() + " " + destination.getY());
            System.out.println("   Viteza: " + edge.getSpeedKmh());
            System.out.println("   |");
            step++;
        }

        System.out.println("🏁 SOSIRE LA DESTINAȚIE");
        System.out.println("======================================================\n");
    }
}
