package uk.ac.ed.inf.pizzadronz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.pizzadronz.data.IsInRegionRequest.Region;
import uk.ac.ed.inf.pizzadronz.data.LngLat;

import java.util.List;

@Service
public class PathDataService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String CENTRAL_AREA_URL = "https://ilp-rest-2024.azurewebsites.net/centralArea";

    /**
     * Fetches the central area boundaries from the external service.
     * @return a Region representing the central area.
     */
    public Region getCentralArea() {
        return restTemplate.getForObject(CENTRAL_AREA_URL, Region.class);
    }

    private static final String NO_FLY_ZONES_URL = "https://ilp-rest-2024.azurewebsites.net/noFlyZones";

    /**
     * Fetches the no-fly zones from the external service.
     * @return a list of regions representing the no-fly zones.
     */
    public List<Region> getNoFlyZones() {
        ResponseEntity<List<Region>> response = restTemplate.exchange(
                NO_FLY_ZONES_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {} // Correct usage for generic types
        );
        List<Region> noFlyZones = response.getBody();
        if (noFlyZones == null || noFlyZones.isEmpty()) {
            throw new IllegalStateException("No-fly zones data is missing or empty.");
        }
        return noFlyZones;
    }

    private static final String RESTAURANT_LOCATION_URL = "https://ilp-rest-2024.azurewebsites.net/restaurants/{name}";

    /**
     * Fetches the location of a specific restaurant by its name.
     * @param restaurantName the name of the restaurant.
     * @return the location of the restaurant as a LngLat.
     */
    public LngLat fetchRestaurantLocation(String restaurantName) {
        return restTemplate.getForObject(RESTAURANT_LOCATION_URL, LngLat.class, restaurantName);
    }


}

