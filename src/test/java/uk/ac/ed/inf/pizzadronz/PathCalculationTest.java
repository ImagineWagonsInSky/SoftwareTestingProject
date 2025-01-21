package uk.ac.ed.inf.pizzadronz;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import uk.ac.ed.inf.pizzadronz.constant.SystemConstants;
import uk.ac.ed.inf.pizzadronz.data.LngLat;
import uk.ac.ed.inf.pizzadronz.data.IsInRegionRequest.Region;
import uk.ac.ed.inf.pizzadronz.data.Position;
import uk.ac.ed.inf.pizzadronz.service.Calculations;
import uk.ac.ed.inf.pizzadronz.service.PathCalculator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class PathCalculationTest {

    @Test
    void testCalculateSimplePath() {
        LngLat start = new LngLat(0.0, 0.0);
        LngLat goal = new LngLat(1.0, 1.0);
        List<Region> noFlyZones = new ArrayList<>();

        Region centralArea = new Region();
        centralArea.setName("Central Area");

        // Define the vertices of the central area polygon
        centralArea.setVertices(List.of(
                new Position(-1.0, -1.0),
                new Position(-1.0, -2.0),
                new Position(-2.0, -1.0),
                new Position(-2.0, -2.0),
                new Position(-3.0, -2.0)
        ));

        PathCalculator pathCalculator = new PathCalculator();
        List<LngLat> path = pathCalculator.calculatePath(start, goal, noFlyZones, centralArea);
        assertFalse(path.isEmpty(), "Path should not be empty.");

        Double distance = Calculations.calculateEuclideanDistance(
                goal.getLng(), goal.getLat(), path.get(path.size() - 1).getLng(), path.get(path.size() - 1).getLat()
        );


        assertTrue(distance < SystemConstants.DRONE_IS_CLOSE_DISTANCE);
    }

    @Test
    void testCalculateSimplePathWithCentralArea() {
        LngLat start = new LngLat(0.0, 0.0);
        LngLat goal = new LngLat(1.0, 1.0);
        List<Region> noFlyZones = new ArrayList<>();

        Region centralArea = new Region();
        centralArea.setName("Central Area");
        centralArea.setVertices(List.of(
                new Position(0.5, 0.5),
                new Position(1.5, 0.5),
                new Position(1.5, 1.5),
                new Position(0.5, 1.5)
        ));

        PathCalculator pathCalculator = new PathCalculator();
        List<LngLat> path = pathCalculator.calculatePath(start, goal, noFlyZones, centralArea);
        assertFalse(path.isEmpty(), "Path should not be empty.");

        Double distance = Calculations.calculateEuclideanDistance(
                goal.getLng(), goal.getLat(), path.get(path.size() - 1).getLng(), path.get(path.size() - 1).getLat()
        );


        assertTrue(distance < SystemConstants.DRONE_IS_CLOSE_DISTANCE);
    }

    @Test
    void testCalculateFromRestaurantToAT() {
        LngLat start = new LngLat(-3.179798972064253, 55.939884084483);
        LngLat goal = new LngLat(SystemConstants.APPLETON_LNG, SystemConstants.APPLETON_LAT);
        List<Region> noFlyZones = new ArrayList<>();
        Region centralArea = new Region();
        centralArea.setName("Central Area");
        centralArea.setVertices(List.of());

        PathCalculator pathCalculator = new PathCalculator();
        List<LngLat> path = pathCalculator.calculatePath(start, goal, noFlyZones, centralArea);
        assertFalse(path.isEmpty(), "Path should not be empty.");

        Double distance = Calculations.calculateEuclideanDistance(
                goal.getLng(), goal.getLat(), path.get(path.size() - 1).getLng(), path.get(path.size() - 1).getLat()
        );


        assertTrue(distance < SystemConstants.DRONE_IS_CLOSE_DISTANCE);
    }

    @Test
    void testCalculateActualPathForRestaurant1() {
        LngLat start = new LngLat(-3.1912869215011597, 55.945535152517735);
        LngLat goal = new LngLat(SystemConstants.APPLETON_LNG, SystemConstants.APPLETON_LAT);


        List<Region> noFlyZones = new ArrayList<>();
        Region noFlyZone1 = new Region();
        noFlyZone1.setName("Dr Elsie Inglis Quadrangle");
        noFlyZone1.setVertices(List.of(
                new Position(-3.1907182931900024, 55.94519570234043),
                new Position(-3.1906163692474365, 55.94498241796357),
                new Position(-3.1900262832641597, 55.94507554227258),
                new Position(-3.190133571624756, 55.94529783810495),
                new Position(-3.1907182931900024, 55.94519570234043)
        ));
        noFlyZones.add(noFlyZone1);

//        Region noFlyZone2 = new Region();
//        noFlyZone2.setName("No-Fly Zone 2");
//        noFlyZone2.setVertices(List.of(
//                new Position(-3.188, 55.945),
//                new Position(-3.188, 55.944),
//                new Position(-3.187, 55.944),
//                new Position(-3.187, 55.945),
//                new Position(-3.188, 55.945) // Close the polygon
//        ));
//        noFlyZones.add(noFlyZone2);


        Region centralArea = new Region();
        centralArea.setName("Central Area");

        // Define the vertices of the central area polygon
        centralArea.setVertices(List.of(
                new Position(-3.192473, 55.946233),
                new Position(-3.192473, 55.942617),
                new Position(-3.184319, 55.942617),
                new Position(-3.184319, 55.946233),
                new Position(-3.192473, 55.946233)
        ));

        PathCalculator pathCalculator = new PathCalculator();
        List<LngLat> path = pathCalculator.calculatePath(start, goal, noFlyZones, centralArea);
        assertFalse(path.isEmpty(), "Path should not be empty.");

        Double distance = Calculations.calculateEuclideanDistance(
                goal.getLng(), goal.getLat(), path.get(path.size() - 1).getLng(), path.get(path.size() - 1).getLat()
        );


        assertTrue(distance < SystemConstants.DRONE_IS_CLOSE_DISTANCE);
    }

    @Test
    void testCalculateActualPathForRestaurant7() {
        LngLat start = new LngLat(-3.179798972064253, 55.939884084483);
        LngLat goal = new LngLat(SystemConstants.APPLETON_LNG, SystemConstants.APPLETON_LAT);
        List<Region> noFlyZones = new ArrayList<>();

        Region centralArea = new Region();
        centralArea.setName("Central Area");

        // Define the vertices of the central area polygon
        centralArea.setVertices(List.of(
                new Position(-3.192473, 55.946233),
                new Position(-3.192473, 55.942617),
                new Position(-3.184319, 55.942617),
                new Position(-3.184319, 55.946233),
                new Position(-3.192473, 55.946233)
        ));

        PathCalculator pathCalculator = new PathCalculator();
        List<LngLat> path = pathCalculator.calculatePath(start, goal, noFlyZones, centralArea);
        assertFalse(path.isEmpty(), "Path should not be empty.");

        Double distance = Calculations.calculateEuclideanDistance(
                goal.getLng(), goal.getLat(), path.get(path.size() - 1).getLng(), path.get(path.size() - 1).getLat()
        );


        assertTrue(distance < SystemConstants.DRONE_IS_CLOSE_DISTANCE);
    }
}
