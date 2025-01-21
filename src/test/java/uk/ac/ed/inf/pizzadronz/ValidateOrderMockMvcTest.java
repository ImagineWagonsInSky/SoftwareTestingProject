package uk.ac.ed.inf.pizzadronz;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ed.inf.pizzadronz.data.Order;
import uk.ac.ed.inf.pizzadronz.data.OrderValidationResult;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ValidateOrderMockMvcTest {

    private static final String ORDERS_URL = "https://ilp-rest-2024.azurewebsites.net/orders";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void validateFetchedOrders() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Fetch orders from the external service
        RestTemplate restTemplate = new RestTemplate();
        Order[] fetchedOrders = restTemplate.getForObject(ORDERS_URL, Order[].class);

        if (fetchedOrders == null) {
            fail("Failed to fetch orders from " + ORDERS_URL);
        }

        List<Order> orders = Arrays.asList(fetchedOrders);

        // Validate each order using MockMvc
        int i = 0;
        for (Order originalOrder : orders) {
            System.out.println("Validating Order: " + originalOrder.getOrderNo());
            System.out.println("Number: " + i);
            i += 1;
            // Make a deep copy of the original order to retain its original values
            Order copiedOrder = objectMapper.readValue(objectMapper.writeValueAsString(originalOrder), Order.class);

            // Perform the POST request
            ResultActions resultActions = mockMvc.perform(post("/validateOrder")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(copiedOrder)));

            // Verify the response
            resultActions.andDo(result -> {
                // Parse the response body
                OrderValidationResult validationResult = objectMapper.readValue(
                        result.getResponse().getContentAsString(),
                        OrderValidationResult.class
                );

                // Compare order status
                assertEquals(originalOrder.getOrderStatus().toString(), validationResult.getOrderStatus().toString(),
                        "Mismatch in order status for order: " + originalOrder.getOrderNo());

                // Compare validation code
                assertEquals(originalOrder.getOrderValidationCode().toString(), validationResult.getOrderValidationCode().toString(),
                        "Mismatch in validation code for order: " + originalOrder.getOrderNo());
            });

        }
    }

}

