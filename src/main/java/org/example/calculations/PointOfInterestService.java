package org.example.calculations;

import org.example.core.*;
import org.example.repo.PointOfInterestRepository;
import org.example.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PointOfInterestService {
    private final PointOfInterestRepository repo;

    private Graph graph;

    public PointOfInterestService(PointOfInterestRepository repo) {
        this.repo = repo;
    }

    public void addCity(String name, String country, double x, double y, double weekdayCongestionFactor, double weekendCongestionFactor) {
        try {
            City city = new City(name, country, x, y, weekdayCongestionFactor, weekendCongestionFactor);
            CityValidator val = new CityValidator();
            val.validate(city);
            repo.addPointOfInterest(city);
        }

        catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public void addGasStation(String name, String country, double x, double y, int averageStopDuration, boolean hasElectricCharger, double chargingPowerKw) {
        try {
            GasStation gasStation = new GasStation(name, country, x, y, averageStopDuration, hasElectricCharger, chargingPowerKw);
            GasStationValidator val = new GasStationValidator();
            val.validate(gasStation);
            repo.addPointOfInterest(gasStation);
        }

        catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public void addHotel(String name, String country, double x, double y, int averageStopDuration, int stars) {
        try {
            Hotel hotel = new Hotel(name, country, x, y, averageStopDuration, stars);
            HotelValidator val = new HotelValidator();
            val.validate(hotel);
            repo.addPointOfInterest(hotel);
        }

        catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public void addRestaurant(String name, String country, double x, double y, int averageStopDuration, String cousineType, double rating) {
        try {
            Restaurant restaurant = new Restaurant(name, country, x, y, averageStopDuration, cousineType, rating);
            RestaurantValidator val = new RestaurantValidator();
            val.validate(restaurant);
            repo.addPointOfInterest(restaurant);
        }

        catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public PointOfInterest findPointOfInterest(String name, String type) {
        try {
            return repo.findPointOfInterestByNameAndType(name, type);
        }

        catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public List<PointOfInterest> getAllPointsOfInterest(String type) {
        return repo.getAllPointsOfInterest(type);
    }

    public List<City> getAllCitiesWithString(String string, List<City> listOfCities) {
        if (string == null || string.isEmpty()) {
            return new ArrayList<>(listOfCities);
        }

        String searchLower = StringUtils.removeDiacritics(string.toLowerCase());
        String[] keywords = searchLower.split("[,\\s]+");
        return listOfCities.stream().filter(c -> {
            String city = StringUtils.removeDiacritics(c.getName().toLowerCase());
            String country = c.getCountry().toLowerCase();
                return Arrays.stream(keywords).allMatch(keyword -> city.contains(keyword) || country.contains(keyword));
            }).limit(5).toList();
    }

    public List<City> getSortedCities(List<City> listOfCities) {
        if (listOfCities.isEmpty()) {
            return new ArrayList<>();
        }

        return listOfCities.stream().sorted(Comparator.comparing(City::getName, String.CASE_INSENSITIVE_ORDER).thenComparing(City::getCountry, String.CASE_INSENSITIVE_ORDER)).toList();
    }

    public List<City> getOnlyCities() {
        return repo.getAllPointsOfInterest("CITY").stream().map(p -> (City) p).toList();
    }

    public List<GasStation> getOnlyGasStations() {
        return repo.getAllPointsOfInterest("GAS_STATION").stream().map(p -> (GasStation) p).toList();
    }

    public List<Hotel> getOnlyHotels() {
        return repo.getAllPointsOfInterest("HOTEL").stream().map(p -> (Hotel) p).toList();
    }

    public List<Restaurant> getOnlyRestaurants() {
        return repo.getAllPointsOfInterest("RESTAURANT").stream().map(p -> (Restaurant) p).toList();
    }

    /**
     * Builds and returns the graph based on the locations from the repository
     * @param maxConnectDistanceKm The maximum connect radius. Only the nodes which have a Havesine Distance <= maxConnectDistanceKm will be connected
     */
    public void buildGraph(double maxConnectDistanceKm) {
        this.graph = new Graph();
        List<PointOfInterest> allPoints = repo.getAllPointsOfInterest("ALL");

        for (PointOfInterest pointOfInterest : allPoints) {
            graph.addNode(pointOfInterest);
        }

        for (int i = 0; i < allPoints.size(); i++) {
            for (int j = i + 1; j < allPoints.size(); j++) {
                PointOfInterest pointOfInterest1 = allPoints.get(i);
                PointOfInterest pointOfInterest2 = allPoints.get(j);

                double distance = CalculationsService.HavesineDistance(pointOfInterest1, pointOfInterest2);

                if (distance <= maxConnectDistanceKm) {
                    boolean p1IsCity = pointOfInterest1 instanceof City;
                    boolean p2IsCity = pointOfInterest2 instanceof City;

                    if (p1IsCity && p2IsCity && distance > 20.0) {
                        try
                        {
                            VirtualPoint vp1 = CalculationsService.createVirtualPointAtDistance(pointOfInterest1, pointOfInterest2, 10.0);
                            VirtualPoint vp2 = CalculationsService.createVirtualPointAtDistance(pointOfInterest2, pointOfInterest1, 10.0);

                            graph.addNode(vp1);
                            graph.addNode(vp2);

                            graph.addUndirectedEdge(pointOfInterest1, vp1);
                            graph.addUndirectedEdge(vp1, vp2);
                            graph.addUndirectedEdge(vp2, pointOfInterest2);
                        }

                        catch (Exception e) {
                            graph.addUndirectedEdge(pointOfInterest1, pointOfInterest2);
                        }
                    }

                    else if (p1IsCity && distance > 10.0 && !p2IsCity) {
                        insertSingleVirtualPoint(pointOfInterest1, pointOfInterest2, 10.0);
                    }

                    else if (p2IsCity && distance > 10.0 && !p1IsCity) {
                        insertSingleVirtualPoint(pointOfInterest2, pointOfInterest1, 10.0);
                    }

                    else if (p1IsCity && p2IsCity && distance > 10.0) {
                        insertSingleVirtualPoint(pointOfInterest1, pointOfInterest2, 10.0);
                    }

                    else {
                        graph.addUndirectedEdge(pointOfInterest1, pointOfInterest2);
                    }
                }
            }
        }
    }

    public void rebuildGraph(double maxConnectDistanceKm) {
        buildGraph(maxConnectDistanceKm);
    }

    public Graph getGraph() {
        if (this.graph == null) {
            buildGraph(200.0);
        }
        return this.graph;
    }

    private void insertSingleVirtualPoint(PointOfInterest source, PointOfInterest destination, double offsetKm) {
        try {
            VirtualPoint virtualPoint = CalculationsService.createVirtualPointAtDistance(source, destination, offsetKm);
            graph.addNode(virtualPoint);

            graph.addUndirectedEdge(source, virtualPoint);
            graph.addUndirectedEdge(virtualPoint, destination);
        }

        catch (Exception e) {
            graph.addUndirectedEdge(source, destination);
        }
    }
}