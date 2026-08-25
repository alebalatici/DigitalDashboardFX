package org.example.repo;

import org.example.core.PointOfInterest;

import java.util.List;

public interface PointOfInterestRepository {
    void addPointOfInterest(PointOfInterest pointOfInterest);
    PointOfInterest findPointOfInterestByNameAndType(String name, String type);
    List<PointOfInterest> getAllPointsOfInterest(String type);
}