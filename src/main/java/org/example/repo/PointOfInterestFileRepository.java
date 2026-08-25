package org.example.repo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.core.PointOfInterest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class PointOfInterestFileRepository extends PointOfInterestMemoryRepository {
    private final String fileName;
    private final ObjectMapper mapper;

    public PointOfInterestFileRepository(String fileName) {
        super();
        this.fileName = fileName;
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        loadFromFile(fileName);
    }

    public void loadFromFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }

        try {
            List<PointOfInterest> loadedPointsOfInterest = mapper.readValue(file, new TypeReference<>() {});
            for (PointOfInterest pointOfInterest : loadedPointsOfInterest) {
                super.addPointOfInterest(pointOfInterest);
            }
        }

        catch (Exception e) {
            throw new RepositoryException("Failed to load PointOfInterest from JSON file: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try {
            File file = new File(fileName);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            mapper.writeValue(file, getAllPointsOfInterest("ALL"));
        }

        catch (Exception e) {
            throw new RepositoryException("Failed to save PointOfInterest to JSON file: " + e.getMessage());
        }
    }

    @Override
    public void addPointOfInterest(PointOfInterest pointOfInterest){
        super.addPointOfInterest(pointOfInterest);
        saveToFile();
    }

    @Override
    public PointOfInterest findPointOfInterestByNameAndType(String name, String type){
        return super.findPointOfInterestByNameAndType(name, type);
    }

    @Override
    public List<PointOfInterest> getAllPointsOfInterest(String type){
        return super.getAllPointsOfInterest(type);
    }
}