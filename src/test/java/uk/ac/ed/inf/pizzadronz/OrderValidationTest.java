package uk.ac.ed.inf.pizzadronz;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import uk.ac.ed.inf.pizzadronz.data.CreditCardInformation;
import uk.ac.ed.inf.pizzadronz.data.Order;

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

}
