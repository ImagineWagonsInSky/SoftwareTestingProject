package uk.ac.ed.inf.pizzadronz.service;

import org.springframework.stereotype.Service;
import uk.ac.ed.inf.pizzadronz.data.IsInRegionRequest;
import uk.ac.ed.inf.pizzadronz.constant.SystemConstants;
import uk.ac.ed.inf.pizzadronz.data.*;

import java.util.Arrays;
import java.util.List;

@Service
public class PathCalculationService {

    private final PathDataService pathDataService;

    public PathCalculationService(PathDataService pathDataService) {
        this.pathDataService = pathDataService;
    }

    /**
     * Calculates the delivery path for the given order.
     *
     * The path is computed from the restaurant location to the "AT" location,
     * avoiding no-fly zones and ensuring compliance with central area constraints.
     *
     * @param order The order for which the path is being calculated.
     * @return A list of LngLat points representing the calculated delivery path.
     */
    public List<LngLat> calcDeliveryPath(Order order) {
        // Extract required details from the Order
        LngLat restaurantLocation = getRestaurantLocation(order);
        LngLat atLocation = getATLocation();
        List<IsInRegionRequest.Region> noFlyZones = pathDataService.getNoFlyZones();
        IsInRegionRequest.Region centralArea = pathDataService.getCentralArea();

        // Perform path calculation using A*
        PathCalculator pathCalculator = new PathCalculator();
        return pathCalculator.calculatePath(restaurantLocation, atLocation, noFlyZones, centralArea);
    }

    /**
     * Retrieves the location of the restaurant that serves the first pizza in the order.
     *
     * The method fetches restaurant data dynamically and matches the first pizza's name
     * to the menu of each restaurant.
     *
     * @param order The order containing pizzas to validate.
     * @return The location of the restaurant serving the first pizza in the order.
     * @throws IllegalArgumentException If no matching restaurant is found for the pizza.
     */
    private LngLat getRestaurantLocation(Order order) {
        // Fetch the list of restaurants
        List<Restaurant> restaurants = PizzaService.fetchRestaurants();

        // Get the name of the first pizza in the order
        String pizzaName = order.getPizzasInOrder()[0].name();

        // Find the restaurant offering this pizza
        for (Restaurant restaurant : restaurants) {
            boolean isPizzaAvailable = Arrays.stream(restaurant.menu())
                    .anyMatch(menuPizza -> menuPizza.name().equals(pizzaName));

            if (isPizzaAvailable) {
                // Return the location of the matching restaurant
                return restaurant.location();
            }
        }

        // If no matching restaurant is found, throw an exception
        throw new IllegalArgumentException("No restaurant found for pizza: " + pizzaName);
    }


    private LngLat getATLocation() {
        // Hardcoded or configurable "AT" location
        return new LngLat(SystemConstants.APPLETON_LNG, SystemConstants.APPLETON_LAT);
    }

}

