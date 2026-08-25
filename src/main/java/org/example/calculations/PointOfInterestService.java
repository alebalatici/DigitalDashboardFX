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

    public double HavesineDistance(PointOfInterest p1, PointOfInterest p2) {
        return Physics.HavesineDistance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
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
}