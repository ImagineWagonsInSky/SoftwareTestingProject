package uk.ac.ed.inf.pizzadronz.service;

import uk.ac.ed.inf.pizzadronz.data.LngLat;
import uk.ac.ed.inf.pizzadronz.data.Position;

import java.util.List;

public class Polygon {

    // Last vertex sometimes is the same as starting vertex, which confuses the algorithm
    private static List<Position> removeRedundantClosingVertex(List<Position> vertices) {
        if (vertices.size() > 1) {
            Position first = vertices.get(0);
            Position last = vertices.get(vertices.size() - 1);

            // Check if the first and last vertices are identical
            if (first.getLng().equals(last.getLng()) && first.getLat().equals(last.getLat())) {
                vertices = vertices.subList(0, vertices.size() - 1); // Remove the last vertex
            }
        }
        return vertices;
    }

    private static boolean linesIntersect(double x1, double y1, double x2, double y2,
                                          double x3, double y3, double x4, double y4) {
        return (orientation(x1, y1, x2, y2, x3, y3) != orientation(x1, y1, x2, y2, x4, y4)) &&
                (orientation(x3, y3, x4, y4, x1, y1) != orientation(x3, y3, x4, y4, x2, y2));
    }

    private static int orientation(double x1, double y1, double x2, double y2, double x3, double y3) {
        double value = (y2 - y1) * (x3 - x2) - (x2 - x1) * (y3 - y2);
        if (value == 0) return 0; // Collinear
        return (value > 0) ? 1 : 2; // Clockwise or counterclockwise
    }


    // Handles cases where polygon is clipped
    public static boolean doesLineIntersectPolygon(LngLat pointA, LngLat pointB, List<Position> polygonVertices) {
        for (int i = 0; i < polygonVertices.size(); i++) {
            Position vertex1 = polygonVertices.get(i);
            Position vertex2 = polygonVertices.get((i + 1) % polygonVertices.size()); // Wrap around to close the polygon

            if (linesIntersect(
                    pointA.getLng(), pointA.getLat(),
                    pointB.getLng(), pointB.getLat(),
                    vertex1.getLng(), vertex1.getLat(),
                    vertex2.getLng(), vertex2.getLat())) {
                return true; // Line intersects polygon edge
            }
        }
        return false;
    }



    // Ray-Casting Algorithm from https://rosettacode.org/wiki/Ray-casting_algorithm
    public static boolean isPointInPolygon(Position point, List<Position> polygonVertices) {
        // Clean up redundant closing vertex
        polygonVertices = removeRedundantClosingVertex(polygonVertices);

        int n = polygonVertices.size();
        boolean inside = false;

        // Loop through each edge of the polygon
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = polygonVertices.get(i).getLng();
            double yi = polygonVertices.get(i).getLat();
            double xj = polygonVertices.get(j).getLng();
            double yj = polygonVertices.get(j).getLat();

            if (isPointOnBorder(point.getLng(), point.getLat(), xi, yi, xj, yj)) {
                return true; // Border is considered inside
            }

            boolean intersect = ((yi > point.getLat()) != (yj > point.getLat())) &&
                    (point.getLng() < (xj - xi) * (point.getLat() - yi) / (yj - yi) + xi);

            if (intersect) {
                inside = !inside;
            }
        }
        return inside;
    }
    private static boolean isPointOnBorder(double px, double py, double x1, double y1, double x2, double y2) {
        double crossProduct = (py - y1) * (x2 - x1) - (px - x1) * (y2 - y1);
        if (Math.abs(crossProduct) > 1e-10) {
            return false;
        }

        double dotProduct = (px - x1) * (x2 - x1) + (py - y1) * (y2 - y1);
        if (dotProduct < 0) {
            return false; // Point is behind the line segment
        }

        double squaredLength = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
        if (dotProduct > squaredLength) {
            return false; // Point is beyond the line segment
        }

        return true;
    }
}