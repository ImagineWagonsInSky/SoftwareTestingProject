package uk.ac.ed.inf.pizzadronz;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class PolygonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenValidRequest_thenReturnsTrue() throws Exception {
        String requestBody = """
                {
                    "position": {
                        "lng": -3.190000,
                        "lat": 55.944000
                    },
                    "region": {
                        "name": "central",
                        "vertices": [
                            {
                                "lng": -3.192473,
                                "lat": 55.946233
                            },
                            {
                                "lng": -3.192473,
                                "lat": 55.942617
                            },
                            {
                                "lng": -3.184319,
                                "lat": 55.942617
                            },
                            {
                                "lng": -3.184319,
                                "lat": 55.946233
                            }
                        ]
                    }
                }
                """;

        mockMvc.perform(post("/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void whenPointOutsidePolygon_thenReturnsFalse() throws Exception {
        String validRequestBody = """
            {
                "position": {
                    "lng": -3.200000,
                    "lat": 55.950000
                },
                "region": {
                    "name": "central",
                    "vertices": [
                        {
                            "lng": -3.192473,
                            "lat": 55.946233
                        },
                        {
                            "lng": -3.192473,
                            "lat": 55.942617
                        },
                        {
                            "lng": -3.184319,
                            "lat": 55.942617
                        },
                        {
                            "lng": -3.184319,
                            "lat": 55.946233
                        }
                    ]
                }
            }
            """;

        mockMvc.perform(post("/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    public void whenSyntacticallyInvalidJson_thenReturnsBadRequest() throws Exception {
        String invalidRequestBody = """
            {
                "position": {
                    "lng": -3.190000
                    // Missing lat field
                },
                "region": {
                    "name": "central",
                    "vertices": [
                        {
                            "lng": -3.192473,
                            "lat": 55.946233
                        }
                    ]
                }
            }
            """;

        mockMvc.perform(post("/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenSemanticallyInvalidData_thenReturnsBadRequest() throws Exception {
        String invalidRequestBody = """
            {
                "position": {
                    "lng": 190.000000,  // Invalid longitude
                    "lat": 55.944000
                },
                "region": {
                    "name": "central",
                    "vertices": [
                        {
                            "lng": -3.192473,
                            "lat": 55.946233
                        },
                        {
                            "lng": -3.192473,
                            "lat": 55.942617
                        },
                        {
                            "lng": -3.184319,
                            "lat": 55.942617
                        },
                        {
                            "lng": -3.184319,
                            "lat": 55.946233
                        }
                    ]
                }
            }
            """;

        mockMvc.perform(post("/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenPointOutsidePolygon_thenReturnsFalseActualExample() throws Exception {
        String validRequestBody = """
            {
                "position": {
                    "lng": -3.1912869215011597,
                    "lat": 55.945535152517735
                },
                "region": {
                    "name":"Dr Elsie Inglis Quadrangle",
                    "vertices":[
                    {
                        "lng":-3.1907182931900024,
                        "lat":55.94519570234043
                    },
                    {
                        "lng":-3.1906163692474365,
                        "lat":55.94498241796357
                    },
                    {
                        "lng":-3.1900262832641597,
                        "lat":55.94507554227258
                    },
                    {
                        "lng":-3.190133571624756,
                        "lat":55.94529783810495
                    },
                    {
                        "lng":-3.1907182931900024,
                        "lat":55.94519570234043
                    }
                  ]
                }
            }
            """;

        mockMvc.perform(post("/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

}
