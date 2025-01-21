package uk.ac.ed.inf.pizzadronz;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.ac.ed.inf.pizzadronz.constant.OrderStatus;
import uk.ac.ed.inf.pizzadronz.constant.OrderValidationCode;
import uk.ac.ed.inf.pizzadronz.data.*;
import uk.ac.ed.inf.pizzadronz.gsonUtils.GeoJsonExporter;
import uk.ac.ed.inf.pizzadronz.service.Calculations;
import uk.ac.ed.inf.pizzadronz.service.InputValidator;
import uk.ac.ed.inf.pizzadronz.service.PathCalculationService;
import uk.ac.ed.inf.pizzadronz.service.Polygon;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    public static final Double DISTANCE = 0.00015;
    private final PathCalculationService pathCalculationService;

    public RestController(PathCalculationService pathCalculationService) {
        this.pathCalculationService = pathCalculationService;
    }

    @GetMapping("/uuid")
    public String getUUID() {
        return "s2335697";
    }

    @PostMapping("/distanceTo")
    public ResponseEntity<?> distanceTo(@RequestBody LngLatPairRequest request) {
        // Validate input
        if (InputValidator.isNotValidPosition(request.getPosition1()) || InputValidator.isNotValidPosition(request.getPosition2())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid positions data");
        }

        Double distance = Calculations.calculateEuclideanDistance(
                request.getPosition1().getLng(), request.getPosition1().getLat(),
                request.getPosition2().getLng(), request.getPosition2().getLat()
        );

        return ResponseEntity.ok(distance);
    }

    @PostMapping("/isCloseTo")
    public ResponseEntity<?> isCloseTo(@RequestBody LngLatPairRequest request) {

        if (InputValidator.isNotValidPosition(request.getPosition1()) || InputValidator.isNotValidPosition(request.getPosition2())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid positions data");
        }

        Double distance = Calculations.calculateEuclideanDistance(
                request.getPosition1().getLng(), request.getPosition1().getLat(),
                request.getPosition2().getLng(), request.getPosition2().getLat()
        );
        if (distance < DISTANCE) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }

    @PostMapping("/nextPosition")
    public ResponseEntity<?> nextPosition(@RequestBody NextPositionRequest request) {
        if (InputValidator.isNotValidPosition(request.getStart()) || InputValidator.isNotValidAngle(request.getAngle())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid positions data");
        }

        LngLat nextPosition = Calculations.calculateNextPosition(request.getStart().getLng(), request.getStart().getLat(), request.getAngle());
        return ResponseEntity.ok(nextPosition);
    }

    @PostMapping("/isInRegion")
    public ResponseEntity<?> isInRegion(@RequestBody IsInRegionRequest request) {
        if (InputValidator.isNotValidVertices(request.getRegion().getVertices()) || InputValidator.isNotValidPosition(request.getPosition()) || InputValidator.isNotValidRegion(request.getRegion().getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid region data");
        }
        boolean isInsidePolygon = Polygon.isPointInPolygon(request.getPosition(), request.getRegion().getVertices());

        return ResponseEntity.ok(isInsidePolygon);
    }

    @PostMapping("/validateOrder")
    public ResponseEntity<?> validateOrder(@RequestBody Order request) {
        if (request == null || InputValidator.isNotValidOrder(request)) {
            assert request != null;
            OrderValidationResult result = new OrderValidationResult(request.getOrderStatus(), request.getOrderValidationCode());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(result);
        }
        else {
            OrderValidationResult result = new OrderValidationResult(request.getOrderStatus(), request.getOrderValidationCode());
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/calcDeliveryPath")
    public ResponseEntity<?> calcDeliveryPath(@RequestBody Order request) {
        if (request == null || InputValidator.isNotValidOrder(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid order");
        }
        else {
            List<LngLat> result = pathCalculationService.calcDeliveryPath(request);
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/calcDeliveryPathAsGeoJson")
    public ResponseEntity<String> calcDeliveryPathAsGeoJson(@RequestBody Order request) {
        if (request == null || request.getOrderValidationCode() != OrderValidationCode.NO_ERROR || request.getOrderStatus() != OrderStatus.VALID) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid order");
        }

        else {
            List<LngLat> result = pathCalculationService.calcDeliveryPath(request);
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No valid path found.");
            }
            // Generate GeoJSON
            String geoJson = GeoJsonExporter.generateGeoJson(result);

            // Return GeoJSON as the response
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(geoJson);
        }
    }

}
