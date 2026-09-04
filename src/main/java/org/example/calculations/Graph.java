package org.example.calculations;

import org.example.core.PointOfInterest;

import java.util.*;

public class Graph {
    /**
     * The adjacency list, for each node we have a list of edges that are connected to it
     */
    private final Map<PointOfInterest, List<Edge>> adjacencyList;

    public Graph() {
        adjacencyList = new HashMap<>();
    }

    /**
     * Adds a new node (Point of Interest) in the graph if it doesn't already exist
     * @param pointOfInterest The point of interest to be added
     */
    public void addNode(PointOfInterest pointOfInterest) {
        adjacencyList.putIfAbsent(pointOfInterest, new ArrayList<>());
    }

    /**
     * Adds a directed edge to the grapth
     * @param source The source node (Point of Interest)
     * @param destination The destination node (Point of Interest)
     */
    public void addDirectedEdge(PointOfInterest source, PointOfInterest destination) {
        addNode(source);
        addNode(destination);

        Edge edge = new Edge(source, destination);
        adjacencyList.get(source).add(edge);
    }

    /**
     * Adds an undirected edge to the graph
     * @param source The source node (Point of Interest)
     * @param destination The destination node (Point of Interest)
     */
    public void addUndirectedEdge(PointOfInterest source, PointOfInterest destination) {
        addDirectedEdge(source, destination);
        addDirectedEdge(destination, source);
    }

    /**
     * Returns the Adjacency list
     * @return the Adjacency list
     */
    public Map<PointOfInterest, List<Edge>> getAdjacencyList() {
        return adjacencyList;
    }

    /**
     * Returns the edges from a given node
     * @param pointOfInterest The given node
     * @return The edges from the Point of Interest
     */
    public List<Edge> getEdgesFrom(PointOfInterest pointOfInterest) {
        return adjacencyList.getOrDefault(pointOfInterest, Collections.emptyList());
    }

    /**
     * Returns all the nodes (Points of Interest) from the graph
     * @return All the nodes (Points of Interest) from the graph
     */
    public Set<PointOfInterest> getAllNodes() {
        return adjacencyList.keySet();
    }

    /**
     * Finds a Point of Interest in the graph after a name
     * @param name The name given
     * @return The Point of Interest that was found
     */
    public PointOfInterest findByName(String name) {
        if (name == null) {
            return null;
        }

        return adjacencyList.keySet().stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * Clears the whole graph
     */
    public void clear() {
        adjacencyList.clear();
    }
}