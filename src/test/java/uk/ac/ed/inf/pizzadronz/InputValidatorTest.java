package uk.ac.ed.inf.pizzadronz;

import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.pizzadronz.constant.OrderStatus;
import uk.ac.ed.inf.pizzadronz.constant.OrderValidationCode;
import uk.ac.ed.inf.pizzadronz.data.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import uk.ac.ed.inf.pizzadronz.service.InputValidator;
import uk.ac.ed.inf.pizzadronz.service.PizzaService;

class InputValidatorTest {


    @Test
    void testInvalidPizza() {
        Pizza nullPizza = null;
        Pizza noNamePizza = new Pizza(null, 1000);

        assertTrue(InputValidator.isNotValidPizza(nullPizza), "Null pizza should fail.");
        assertTrue(InputValidator.isNotValidPizza(noNamePizza), "Pizza with no name should fail.");
    }

    @Test
    void testValidCreditCard() {
        CreditCardInformation validCard = new CreditCardInformation("1234567812345678", "12/25", "123");
        Order order = new Order();
        assertFalse(InputValidator.isNotValidCreditCard(validCard, order), "Valid credit card should pass.");
    }

    @Test
    void testInvalidCreditCardNumber() {
        CreditCardInformation invalidCard = new CreditCardInformation("12345678", "12/25", "123");
        Order order = new Order();

        assertTrue(InputValidator.isNotValidCreditCard(invalidCard, order), "Invalid credit card number should fail.");
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @Test
    void testExpiredCreditCard() {
        CreditCardInformation expiredCard = new CreditCardInformation("1234567812345678", "12/19", "123");
        Order order = new Order();

        assertTrue(InputValidator.isNotValidCreditCard(expiredCard, order), "Expired credit card should fail.");
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @Test
    void testInvalidCreditCardExpiryFormat() {
        CreditCardInformation invalidFormatCard = new CreditCardInformation("1234567812345678", "25/12", "123");
        Order order = new Order();

        assertTrue(InputValidator.isNotValidCreditCard(invalidFormatCard, order), "Invalid expiry date format should fail.");
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @Test
    void testInvalidCreditCardCVV() {
        CreditCardInformation invalidCvvCard = new CreditCardInformation("1234567812345678", "12/25", "12");
        Order order = new Order();

        assertTrue(InputValidator.isNotValidCreditCard(invalidCvvCard, order), "Invalid CVV should fail.");
        assertEquals(OrderValidationCode.CVV_INVALID, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @Test
    void testEmptyOrderFailsValidation() {
        Order emptyOrder = new Order();
        emptyOrder.setPizzasInOrder(new Pizza[0]);

        assertTrue(InputValidator.isNotValidOrder(emptyOrder), "Empty order should fail validation.");
        assertEquals(OrderValidationCode.EMPTY_ORDER, emptyOrder.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, emptyOrder.getOrderStatus());
    }

    @Test
    void testOrderWithTooManyPizzasFailsValidation() {
        Pizza[] pizzas = {
                new Pizza("R1: Margarita", 1000),
                new Pizza("R1: Margarita", 1000),
                new Pizza("R1: Margarita", 1000),
                new Pizza("R1: Margarita", 1000),
                new Pizza("R1: Margarita", 1000)
        };
        Order order = new Order();
        order.setPizzasInOrder(pizzas);

        assertTrue(InputValidator.isNotValidOrder(order), "Order with more than 4 pizzas should fail.");
        assertEquals(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @Test
    void testOrderWithInvalidPizzaPrice() {
        Pizza[] pizzas = {new Pizza("R1: Margarita", -100)};
        Order order = new Order();
        order.setPizzasInOrder(pizzas);

        assertTrue(InputValidator.isNotValidOrder(order), "Order with invalid pizza price should fail.");
        assertEquals(OrderValidationCode.PRICE_FOR_PIZZA_INVALID, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @Test
    void testOrderWithIncorrectTotalPrice() {
        Pizza[] pizzas = {new Pizza("R1: Margarita", 1000)};
        Order order = new Order();
        order.setPizzasInOrder(pizzas);
        order.setPriceTotalInPence(1200); // Incorrect total

        assertTrue(InputValidator.isNotValidOrder(order), "Order with incorrect total price should fail.");
        assertEquals(OrderValidationCode.TOTAL_INCORRECT, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

}
