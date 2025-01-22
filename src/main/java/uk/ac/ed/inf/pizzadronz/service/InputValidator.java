package uk.ac.ed.inf.pizzadronz.service;

import uk.ac.ed.inf.pizzadronz.constant.OrderStatus;
import uk.ac.ed.inf.pizzadronz.constant.OrderValidationCode;
import uk.ac.ed.inf.pizzadronz.data.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static uk.ac.ed.inf.pizzadronz.constant.SystemConstants.MAX_PIZZAS_PER_ORDER;
import static uk.ac.ed.inf.pizzadronz.constant.SystemConstants.ORDER_CHARGE_IN_PENCE;

/**
 * Utility class for validating various inputs such as positions, angles, orders, and credit card information.
 */
public class InputValidator {

    /**
     * Validates if a position is valid.
     * A position is invalid if it is null or its longitude/latitude are out of acceptable ranges.
     *
     * @param position the position to validate
     * @return true if the position is invalid, false otherwise
     */
    public static boolean isNotValidPosition(Position position) {
        if (position == null) return true;
        Double lng = position.getLng();
        Double lat = position.getLat();
        if (lng == null || lat == null) return true;

        // Longitude should be between -180 and 180, latitude between -90 and 90
        return !(lng >= -180) || !(lng <= 180) || !(lat >= -90) || !(lat <= 90);
    }

    /**
     * Validates if an angle is within the acceptable range (0 to 360 degrees).
     *
     * @param angle the angle to validate
     * @return true if the angle is invalid, false otherwise
     */
    public static boolean isNotValidAngle(Double angle) {
        if (angle == null) return true;
        return angle < 0 || angle > 360;
    }

    /**
     * Validates if a list of vertices forms a valid polygon.
     * A polygon is invalid if it has fewer than 3 vertices or any vertex is invalid.
     *
     * @param vertices the list of vertices
     * @return true if the vertices are invalid, false otherwise
     */
    public static boolean isNotValidVertices(List<Position> vertices) {
        if (vertices == null || vertices.size() < 3) return true;
        return vertices.stream().anyMatch(InputValidator::isNotValidPosition);
    }

    /**
     * Validates if a region name is valid.
     * A region name is invalid if it is null.
     *
     * @param name the region name
     * @return true if the region name is invalid, false otherwise
     */
    public static boolean isNotValidRegion(String name) {
        return name == null || name.isEmpty();
    }

    /**
     * Validates if a pizza is valid.
     * A pizza is invalid if it is null or its name is null.
     *
     * @param pizza the pizza to validate
     * @return true if the pizza is invalid, false otherwise
     */
    public static boolean isNotValidPizza(Pizza pizza) {
        return pizza == null || pizza.name() == null;
    }

    /**
     * Validates credit card information.
     * This includes checks for card number length, expiry format, and CVV format.
     *
     * @param creditCardInfo the credit card information
     * @param order          the associated order (to set validation codes)
     * @return true if the credit card information is invalid, false otherwise
     */
    public static boolean isNotValidCreditCard(CreditCardInformation creditCardInfo, Order order) {
        if (creditCardInfo == null) return true;

        // Check credit card number (16-digit length)
        if (creditCardInfo.getCreditCardNumber() == null || creditCardInfo.getCreditCardNumber().length() != 16) {
            order.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            order.setOrderStatus(OrderStatus.INVALID);
            return true;
        }

        // Check expiry date format (MM/YY)
        if (!creditCardInfo.getCreditCardExpiry().matches("\\d{2}/\\d{2}")) {
            order.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            order.setOrderStatus(OrderStatus.INVALID);
            return true;
        }

        try {
            // Parse expiry date
            String[] parts = creditCardInfo.getCreditCardExpiry().split("/");
            int expiryMonth = Integer.parseInt(parts[0]);
            int expiryYear = Integer.parseInt(parts[1]) + 2000; // Convert to 4-digit year

            // Validate month range
            if (expiryMonth < 1 || expiryMonth > 12) {
                order.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
                order.setOrderStatus(OrderStatus.INVALID);
                return true;
            }

            // Create a LocalDate for the last day of the expiry month
            LocalDate expiryDate = LocalDate.of(expiryYear, expiryMonth, 1).plusMonths(1).minusDays(1);

            // Get the current date
            LocalDate today = LocalDate.now();

            // Check if expiry date is in the future
            if (!expiryDate.isAfter(today)) {
                order.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
                order.setOrderStatus(OrderStatus.INVALID);
                return true;
            }
        } catch (Exception e) {
            // Handle any parsing or date-related exceptions
            order.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            order.setOrderStatus(OrderStatus.INVALID);
            return true;
        }


        // Check CVV (3 digits)
        if (creditCardInfo.getCvv() == null || !creditCardInfo.getCvv().matches("\\d{3}")) {
            order.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            order.setOrderStatus(OrderStatus.INVALID);
            return true;
        }

        return false;
    }

    // Order validation
    /**
     * Validates an order.
     * This includes checks for pizza count, pizza availability, total price, credit card validity, and restaurant constraints.
     *
     * @param order the order to validate
     * @return true if the order is invalid, false otherwise
     */
    public static boolean isNotValidOrder(Order order) {
        if (order == null) return true;

        // Validate order number
        if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) return true;

        // Validate the number of pizzas
        int numberOfPizzas = order.getPizzasInOrder().length;
        if (numberOfPizzas < 1) {
            order.setOrderValidationCode(OrderValidationCode.EMPTY_ORDER);
            order.setOrderStatus(OrderStatus.INVALID);
            return true;
        }
        if (numberOfPizzas > MAX_PIZZAS_PER_ORDER) {
            order.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
            order.setOrderStatus(OrderStatus.INVALID);
            return true;
        }

        // Fetch restaurant data
        List<Restaurant> restaurants = PizzaService.fetchRestaurants();

        // Validate each pizza
        for (Pizza pizza : order.getPizzasInOrder()) {
            if (!restaurants.stream().anyMatch(restaurant -> Arrays.stream(restaurant.menu())
                    .anyMatch(menuItem -> menuItem.name().equals(pizza.name())))) {
                order.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
                order.setOrderStatus(OrderStatus.INVALID);
                return true;
            }
        }

        // Validate total price
        int calculatedTotal = Arrays.stream(order.getPizzasInOrder())
                .mapToInt(Pizza::priceInPence)
                .sum();
        if (calculatedTotal + ORDER_CHARGE_IN_PENCE != order.getPriceTotalInPence()) {
            order.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
            order.setOrderStatus(OrderStatus.INVALID);
            return true;
        }
        // Validate if price for pizza is invalid
        for (Pizza pizza : order.getPizzasInOrder()) {
            if (pizza.priceInPence() <= 0) {
                order.setOrderValidationCode(OrderValidationCode.PRICE_FOR_PIZZA_INVALID);
                order.setOrderStatus(OrderStatus.INVALID);
                return true;
            }
        }

        // Validate credit card information
        if (isNotValidCreditCard(order.getCreditCardInformation(), order)) return true;

        // Additional restaurant constraints (multiple restaurants, closed restaurants, etc.)
        if (!validateRestaurantConstraints(order, restaurants)) return true;

        order.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        order.setOrderStatus(OrderStatus.VALID);
        return false;
    }

    /**
     * Validates that the given order satisfies all restaurant-related constraints.
     *
     * If any constraint is violated, the appropriate OrderValidationCode and OrderStatus
     * are set on the order, and the function returns false.
     *
     * @param order       The order to validate.
     * @param restaurants A list of available restaurants with their menus and details.
     * @return true if all restaurant-related constraints are satisfied; false otherwise.
     */
    private static boolean validateRestaurantConstraints(Order order, List<Restaurant> restaurants) {
        String commonRestaurant = null;

        // Validate that all pizzas belong to the same restaurant
        for (Pizza pizza : order.getPizzasInOrder()) {
            // Find the restaurant that offers this pizza
            Restaurant matchingRestaurant = restaurants.stream()
                    .filter(restaurant -> Arrays.stream(restaurant.menu())
                            .anyMatch(menuItem -> menuItem.name().equals(pizza.name())))
                    .findFirst()
                    .orElse(null);

            // If no restaurant offers this pizza, set the appropriate error code
            if (matchingRestaurant == null) {
                order.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
                order.setOrderStatus(OrderStatus.INVALID);
                return false;
            }

            // Check if the price matches the known price for this pizza
            boolean isPriceValid = Arrays.stream(matchingRestaurant.menu())
                    .anyMatch(menuItem -> menuItem.name().equals(pizza.name()) &&
                            menuItem.priceInPence() == pizza.priceInPence());

            if (!isPriceValid) {
                order.setOrderValidationCode(OrderValidationCode.PRICE_FOR_PIZZA_INVALID);
                order.setOrderStatus(OrderStatus.INVALID);
                return false; // Invalid price for a recognized pizza
            }

            // Check for consistent restaurant
            if (commonRestaurant == null) {
                commonRestaurant = matchingRestaurant.name();
            } else if (!commonRestaurant.equals(matchingRestaurant.name())) {
                order.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
                order.setOrderStatus(OrderStatus.INVALID);
                return false; // Pizzas are from different restaurants
            }
        }

        // Validate if the restaurant is open on the order date
        DayOfWeek orderDay = order.getOrderDate().getDayOfWeek();
        String finalCommonRestaurant = commonRestaurant;

        boolean isRestaurantOpen = restaurants.stream()
                .filter(restaurant -> restaurant.name().equals(finalCommonRestaurant))
                .anyMatch(restaurant -> Arrays.asList(restaurant.openingDays()).contains(orderDay));

        if (!isRestaurantOpen) {
            order.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
            order.setOrderStatus(OrderStatus.INVALID);
            return false; // Restaurant is closed
        }

        return true; // All restaurant constraints are satisfied
    }

}
