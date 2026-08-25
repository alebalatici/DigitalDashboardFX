package org.example.repo;

import org.example.core.PointOfInterest;

import java.util.ArrayList;
import java.util.List;

public class PointOfInterestMemoryRepository implements PointOfInterestRepository {
    protected final List<PointOfInterest> pointsOfInterest = new ArrayList<>();

    public PointOfInterestMemoryRepository() {

    }

    @Override
    public void addPointOfInterest(PointOfInterest pointOfInterest) {
        if (pointOfInterest == null) {
            throw new NullPointerException("Cannot add a null PointOfInterest");
        }

        String type = pointOfInterest.getType();

        boolean exists = pointsOfInterest.stream().anyMatch(p -> p.getName().equalsIgnoreCase(pointOfInterest.getName()) && p.getType().equalsIgnoreCase(type));

        if (exists) {
            throw new RepositoryException("A " + type + " with the name " + pointOfInterest.getName() + " already exists in the system");
        }

        pointsOfInterest.add(pointOfInterest);
    }

    @Override
    public PointOfInterest findPointOfInterestByNameAndType(String name, String type) {
        return pointsOfInterest.stream().filter(p -> p.getName().equalsIgnoreCase(name) && p.getType().equalsIgnoreCase(type)).findFirst().orElseThrow(() -> new RepositoryException("A " + type + " with the name " + name + " does not exist in the system"));
    }

    @Override
    public List<PointOfInterest> getAllPointsOfInterest(String type) {
        if (type == null || type.isEmpty() || type.equalsIgnoreCase("ALL")) {
            return new ArrayList<>(pointsOfInterest);
        }

        List<PointOfInterest> filteredList =  new ArrayList<>();
        for (PointOfInterest poi : pointsOfInterest) {
            if (poi.getType().equalsIgnoreCase(type)) {
                filteredList.add(poi);
            }
        }

        return filteredList;
    }
}