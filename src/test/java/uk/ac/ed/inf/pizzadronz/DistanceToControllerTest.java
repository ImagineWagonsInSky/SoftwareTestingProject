package uk.ac.ed.inf.pizzadronz;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
public class DistanceToControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenValidRequest_thenReturnsCorrectDistance() throws Exception {
        String requestBody = """
                {
                    "position1": {
                        "lng": -3.192473,
                        "lat": 55.946233
                    },
                    "position2": {
                        "lng": -3.192473,
                        "lat": 55.942617
                    }
                }
                """;

        mockMvc.perform(post("/distanceTo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void whenInvalidLongitude_thenReturnsBadRequest() throws Exception {
        String invalidRequestBody = """
                {
                    "position1": {
                        "lng": 180.000001,
                        "lat": 55.946233
                        },
                    "position2": {
                        "lng": -3.192473,
                        "lat": 55.942617
                        }
                    }
                """;

        mockMvc.perform(post("/distanceTo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenInvalidLatitude_thenReturnsBadRequest() throws Exception {
        String invalidRequestBody = """
                {
                    "position1": {
                        "lng": -3.192473,
                        "lat": 90.000001
                        },
                    "position2": {
                        "lng": -3.192473,
                        "lat": 55.942617
                        }
                    }
                """;

        mockMvc.perform(post("/distanceTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenEmptyBody_thenReturnsBadRequest() throws Exception {
        String emptyRequestBody = "{}";
        mockMvc.perform(post("/distanceTo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(emptyRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenMissingFields_thenReturnsBadRequest() throws Exception {
        String missingFieldBody = """
                {
                    "position1": {
                        "lng": -3.192473,
                        "lat": 55.946233
                    },
                    "position2": {
                        "lng": -3.192473
                    }
                }
                """;
        mockMvc.perform(post("/distanceTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(missingFieldBody))
                .andExpect(status().isBadRequest());
    }
}
