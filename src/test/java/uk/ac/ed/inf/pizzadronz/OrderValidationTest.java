package uk.ac.ed.inf.pizzadronz;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import uk.ac.ed.inf.pizzadronz.constant.OrderValidationCode;
import uk.ac.ed.inf.pizzadronz.data.CreditCardInformation;
import uk.ac.ed.inf.pizzadronz.data.Order;
import uk.ac.ed.inf.pizzadronz.data.Pizza;
import uk.ac.ed.inf.pizzadronz.service.InputValidator;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ac.ed.inf.pizzadronz.service.InputValidator.isNotValidCreditCard;


@SpringBootTest
public class OrderValidationTest {

    @Test
    void testCreditCardExpiryValidationWithDateExistence() {
        CreditCardInformation validCard = new CreditCardInformation("1234567812345678", "12/25", "123");
        CreditCardInformation expiredCard = new CreditCardInformation("1234567812345678", "12/19", "123");
        CreditCardInformation invalidMonthCard = new CreditCardInformation("1234567812345678", "13/25", "123");
        CreditCardInformation invalidDayCard = new CreditCardInformation("1234567812345678", "30/02", "123");

        assertFalse(isNotValidCreditCard(validCard, new Order()), "Valid card should pass.");
        assertTrue(isNotValidCreditCard(expiredCard, new Order()), "Expired card should fail.");
        assertTrue(isNotValidCreditCard(invalidMonthCard, new Order()), "Card with invalid month should fail.");
        assertTrue(isNotValidCreditCard(invalidDayCard, new Order()), "Card with invalid day should fail.");
    }

    @Test
    void testPizzaCountValidation() {
        // Order with no pizzas
        Order emptyOrder = new Order();
        emptyOrder.setPizzasInOrder(new Pizza[0]);

        // Order with too many pizzas
        Pizza[] manyPizzas = new Pizza[5];
        Arrays.fill(manyPizzas, new Pizza("R1: Margarita", 1000));
        Order overLimitOrder = new Order();
        overLimitOrder.setPizzasInOrder(manyPizzas);

        // Valid order with correct pizza count
        Pizza[] validPizzas = {
                new Pizza("R1: Margarita", 1000),
                new Pizza("R1: Calzone", 1400)
        };
        Order validOrder = new Order();
        validOrder.setPizzasInOrder(validPizzas);

        assertTrue(InputValidator.isNotValidOrder(emptyOrder), "Order with no pizzas should be invalid.");
        assertTrue(InputValidator.isNotValidOrder(overLimitOrder), "Order with too many pizzas should be invalid.");
        assertFalse(InputValidator.isNotValidOrder(validOrder), "Order with valid pizza count should pass.");
    }

    @Test
    void testPizzaNotDefinedValidation() {
        // Order with a pizza not on the menu
        Pizza[] invalidPizzas = {
                new Pizza("R1: NonExistentPizza", 1000)
        };
        Order order = new Order();
        order.setPizzasInOrder(invalidPizzas);

        assertTrue(InputValidator.isNotValidOrder(order), "Order with undefined pizza should be invalid.");
        assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, order.getOrderValidationCode(), "Validation code should indicate pizza not defined.");
    }

    @Test
    void testPriceValidation() {
        // Order with incorrect total price
        Pizza[] pizzas = {
                new Pizza("R1: Margarita", 1000),
                new Pizza("R1: Calzone", 1400)
        };
        Order order = new Order();
        order.setPizzasInOrder(pizzas);
        order.setPriceTotalInPence(2500); // Incorrect total

        assertTrue(InputValidator.isNotValidOrder(order), "Order with incorrect price total should be invalid.");
        assertEquals(OrderValidationCode.TOTAL_INCORRECT, order.getOrderValidationCode(), "Validation code should indicate incorrect total.");
    }

    @Test
    void testCreditCardValidation() {
        // Valid card
        CreditCardInformation validCard = new CreditCardInformation("1234567812345678", "12/25", "123");

        // Invalid card number
        CreditCardInformation invalidCardNumber = new CreditCardInformation("1234", "12/25", "123");

        // Invalid CVV
        CreditCardInformation invalidCvv = new CreditCardInformation("1234567812345678", "12/25", "12");

        Order order = new Order();

        assertFalse(InputValidator.isNotValidCreditCard(validCard, order), "Valid card should pass.");
        assertTrue(InputValidator.isNotValidCreditCard(invalidCardNumber, order), "Invalid card number should fail.");
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, order.getOrderValidationCode(), "Validation code should indicate card number invalid.");
        assertTrue(InputValidator.isNotValidCreditCard(invalidCvv, order), "Invalid CVV should fail.");
        assertEquals(OrderValidationCode.CVV_INVALID, order.getOrderValidationCode(), "Validation code should indicate CVV invalid.");
    }

    @Test
    void testPizzasFromMultipleRestaurantsValidation() {
        // Order with pizzas from different restaurants
        Pizza[] pizzas = {
                new Pizza("R1: Margarita", 1000),
                new Pizza("R2: Meat Lover", 1400)
        };
        Order order = new Order();
        order.setPizzasInOrder(pizzas);

        assertTrue(InputValidator.isNotValidOrder(order), "Order with pizzas from multiple restaurants should be invalid.");
        assertEquals(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, order.getOrderValidationCode(), "Validation code should indicate multiple restaurant issue.");
    }

    @Test
    void testRestaurantClosedValidation() {
        // Restaurant closed on the order date
        Pizza[] pizzas = {
                new Pizza("R1: Margarita", 1000)
        };
        Order order = new Order();
        order.setPizzasInOrder(pizzas);
        order.setOrderDate(LocalDate.of(2025, 1, 1)); // Assume this is a day when the restaurant is closed

        assertTrue(InputValidator.isNotValidOrder(order), "Order placed on a closed restaurant day should be invalid.");
        assertEquals(OrderValidationCode.RESTAURANT_CLOSED, order.getOrderValidationCode(), "Validation code should indicate restaurant is closed.");
    }

}
