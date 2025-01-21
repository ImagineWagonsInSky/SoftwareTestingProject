package uk.ac.ed.inf.pizzadronz.gsonUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ed.inf.pizzadronz.data.LngLat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeoJsonExporter {
    public static String generateGeoJson(List<LngLat> path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path is null or empty. Cannot generate GeoJSON.");
        }

        // Define GeoJSON structure
        Map<String, Object> geoJson = new HashMap<>();
        geoJson.put("type", "FeatureCollection");

        Map<String, Object> feature = new HashMap<>();
        feature.put("type", "Feature");

        Map<String, Object> geometry = new HashMap<>();
        geometry.put("type", "LineString");

        // Ensure coordinates are valid
        List<List<Double>> coordinates = path.stream()
                .map(point -> {
                    if (point.getLng() == null || point.getLat() == null) {
                        throw new IllegalArgumentException("Invalid LngLat in path: " + point);
                    }
                    return List.of(point.getLng(), point.getLat());
                })
                .toList();

        geometry.put("coordinates", coordinates);
        feature.put("geometry", geometry);

        // Some properties for metadata
        Map<String, Object> properties = new HashMap<>();
        properties.put("pathName", "Drone Flight Path");
        properties.put("pathLength", path.size());
        feature.put("properties", properties);

        geoJson.put("features", List.of(feature));

        try {
            // Serialize GeoJSON to string
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(geoJson);
        } catch (Exception e) {
            throw new RuntimeException("Error generating GeoJSON", e);
        }
    }
}