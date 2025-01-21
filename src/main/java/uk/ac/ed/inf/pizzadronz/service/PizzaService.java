package uk.ac.ed.inf.pizzadronz.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.pizzadronz.data.Restaurant;

import java.util.List;

@Service
public class PizzaService {
    private static final String RESTAURANT_API_URL = "https://ilp-rest-2024.azurewebsites.net/restaurants";


    private static RestTemplate restTemplate = null;
    private static ObjectMapper objectMapper = null;

    public PizzaService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        PizzaService.restTemplate = restTemplate;
        PizzaService.objectMapper = objectMapper;
    }

    /**
     * Method to fetch restaurant data from external REST service
     */
    public static List<Restaurant> fetchRestaurants() {
        String json = restTemplate.getForObject(RESTAURANT_API_URL, String.class);
        try {
            return objectMapper.readValue(json, new TypeReference<List<Restaurant>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();  // Return an empty list if there's an error
        }
    }
}
