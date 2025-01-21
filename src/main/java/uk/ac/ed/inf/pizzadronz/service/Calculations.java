package uk.ac.ed.inf.pizzadronz.service;

import uk.ac.ed.inf.pizzadronz.RestController;
import uk.ac.ed.inf.pizzadronz.data.LngLat;

public class Calculations {
    /**
     * Calculates the Euclidean distance between two geographical points.
     *
     * @param lng1 Longitude of the first point.
     * @param lat1 Latitude of the first point.
     * @param lng2 Longitude of the second point.
     * @param lat2 Latitude of the second point.
     * @return The Euclidean distance between the two points.
     */
    public static Double calculateEuclideanDistance(Double lng1, Double lat1, Double lng2, Double lat2) {
        return Math.sqrt(Math.pow(lng1 - lng2, 2) + Math.pow(lat1 - lat2, 2));
    }

    /**
     * Calculates the next position of the drone based on the current position
     * and an angle of movement.
     *
     * @param lng   Current longitude of the drone.
     * @param lat   Current latitude of the drone.
     * @param angle Angle of movement in degrees, measured clockwise from north.
     * @return A new LngLat object representing the next position of the drone.
     */
    public static LngLat calculateNextPosition(Double lng, Double lat, Double angle) {
        double angleInRadians = angle * (Math.PI / 180);
        double newLongitude = lng + (RestController.DISTANCE * Math.cos(angleInRadians));
        double newLatitude = lat + (RestController.DISTANCE * Math.sin(angleInRadians));

        return new LngLat(newLongitude, newLatitude);
    }
}
