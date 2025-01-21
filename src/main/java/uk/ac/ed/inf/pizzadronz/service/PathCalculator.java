package uk.ac.ed.inf.pizzadronz.service;

import uk.ac.ed.inf.pizzadronz.constant.SystemConstants;
import uk.ac.ed.inf.pizzadronz.data.LngLat;
import uk.ac.ed.inf.pizzadronz.data.IsInRegionRequest.Region;
import uk.ac.ed.inf.pizzadronz.data.Position;
import uk.ac.ed.inf.pizzadronz.gsonUtils.FileUtils;
import uk.ac.ed.inf.pizzadronz.gsonUtils.GeoJsonExporter;

import java.util.*;

public class PathCalculator {
    /**
     * Calculates the optimal delivery path using the A* algorithm.
     *
     * @param start       The starting position (restaurant location).
     * @param goal        The goal position ("AT" location).
     * @param noFlyZones  A list of no-fly zones to avoid.
     * @param centralArea The central area boundary.
     * @return A list of LngLat positions representing the path.
     */
    public List<LngLat> calculatePath(LngLat start, LngLat goal, List<Region> noFlyZones, Region centralArea) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::getFCost));
        Set<LngLat> closedSet = new HashSet<>();

        Node startNode = new Node(start, 0, heuristic(start, goal), null);
        openSet.add(startNode);

        System.out.println("Starting path calculation:");
        System.out.println("Start: " + start + ", Goal: " + goal);

        int maxIterations = 10000;
        int iterationCount = 0;

        while (!openSet.isEmpty()) {
            iterationCount++;
            if (iterationCount > maxIterations) {
                System.out.println("Too many iterations, possible infinite loop.");
                return new ArrayList<>();
            }

            Node current = openSet.poll();
            System.out.println("Current Node: " + current.position);

            Double distance = Calculations.calculateEuclideanDistance(
                    current.position.getLng(), current.position.getLat(),
                    goal.getLng(), goal.getLat()
            );

            if (distance < SystemConstants.DRONE_IS_CLOSE_DISTANCE) {
                System.out.println("Goal reached. Distance to goal: " + distance);
                List<LngLat> path = reconstructPath(current);

                // Generate GeoJSON for visualization
                String geoJson = GeoJsonExporter.generateGeoJson(path);
                FileUtils.saveToFile(geoJson, "flightpath.geojson");
                System.out.println("GeoJSON saved to flightpath.geojson");

                return path;
            }

            closedSet.add(current.position);

            for (LngLat neighbor : getNeighbors(current.position)) {
                if (closedSet.contains(neighbor) || isInvalidNode(neighbor, noFlyZones, centralArea, current)) {
                    continue;
                }

                double tentativeGCost = current.gCost + stepCost(current.position, neighbor);

                Optional<Node> existingNeighbor = openSet.stream()
                        .filter(n -> n.position.equals(neighbor))
                        .findFirst();

                if (existingNeighbor.isPresent()) {
                    Node existingNode = existingNeighbor.get();
                    if (tentativeGCost < existingNode.gCost) {
                        openSet.remove(existingNode);
                        Node updatedNode = new Node(neighbor, tentativeGCost, heuristic(neighbor, goal), current);
                        openSet.add(updatedNode);
                    }
                    continue;
                }

                Node neighborNode = new Node(neighbor, tentativeGCost, heuristic(neighbor, goal), current);
                openSet.add(neighborNode);
            }
        }

        System.out.println("No valid path found.");
        return new ArrayList<>(); // No valid path found
    }

    private static double heuristic(LngLat current, LngLat goal) {
        double euclideanDistance = Math.sqrt(Math.pow(goal.getLng() - current.getLng(), 2) +
                Math.pow(goal.getLat() - current.getLat(), 2));
        return euclideanDistance * 1.1; // Add slight weight to prioritize closeness
    }

    private List<LngLat> getNeighbors(LngLat position) {
        double stepSize = SystemConstants.DRONE_MOVE_DISTANCE;
        List<LngLat> neighbors = new ArrayList<>();

        // Define 16 directions (angle in degrees)
        double[] angles = {0, 22.5, 45, 67.5, 90, 112.5, 135, 157.5, 180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5};

        for (double angle : angles) {
            double radians = Math.toRadians(angle);
            double lng = position.getLng() + stepSize * Math.cos(radians);
            double lat = position.getLat() + stepSize * Math.sin(radians);
            neighbors.add(new LngLat(lng, lat));
        }

        System.out.println("Neighbors for " + position.getLat() + ", " + position.getLng() + ": " + neighbors);
        return neighbors;
    }

    private boolean isInvalidNode(LngLat node, List<Region> noFlyZones, Region centralArea, Node current) {
        Position nodePosition = new Position(node.getLng(), node.getLat());
        LngLat currentPosition = current.position;

        // Check if the node is inside any no-fly zone
        for (Region noFlyZone : noFlyZones) {
            if (Polygon.isPointInPolygon(nodePosition, noFlyZone.getVertices())) {
                System.out.println("Node in No-Fly Zone: " + node + " in " + noFlyZone.getName());
                return true; // Node is in a no-fly zone
            }

            // Check if the line segment intersects the no-fly zone
            if (Polygon.doesLineIntersectPolygon(currentPosition, node, noFlyZone.getVertices())) {
                System.out.println("Line segment intersects No-Fly Zone: " + noFlyZone.getName());
                return true; // Line segment crosses the polygon
            }
        }

        // Check if the node leaves the central area after entering
        boolean currentInCentralArea = Polygon.isPointInPolygon(new Position(currentPosition.getLng(), currentPosition.getLat()), centralArea.getVertices());
        boolean nodeInCentralArea = Polygon.isPointInPolygon(nodePosition, centralArea.getVertices());

        if (currentInCentralArea && !nodeInCentralArea) {
            return true; // Node exits the central area after entering
        }

        return false; // Node is valid
    }

    private List<LngLat> reconstructPath(Node node) {
        List<LngLat> path = new ArrayList<>();
        while (node != null) {
            path.add(0, node.position); // Add to the start of the list
            node = node.parent;
        }
        return path;
    }

    private double stepCost(LngLat from, LngLat to) {
        return Math.sqrt(Math.pow(to.getLng() - from.getLng(), 2) + Math.pow(to.getLat() - from.getLat(), 2));
    }

    class Node {
        LngLat position;  // Current position
        double gCost;     // Cost from the start node
        double hCost;     // Heuristic cost to the goal
        Node parent;      // Parent node (for path reconstruction)

        public Node(LngLat position, double gCost, double hCost, Node parent) {
            this.position = position;
            this.gCost = gCost;
            this.hCost = hCost;
            this.parent = parent;
        }

        public double getFCost() {
            return gCost + hCost;
        }
    }


}
