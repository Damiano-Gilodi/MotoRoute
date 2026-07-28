package it.motoroute.integration.route;

import it.motoroute.TestcontainersConfiguration;
import it.motoroute.route.domain.Difficulty;
import it.motoroute.route.domain.Route;
import it.motoroute.route.infrastructure.RouteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class CreateRouteIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RouteRepository routeRepository;

    @Test
    void shouldCreateAndPersistRoute() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Passo dello Stelvio",
                      "description": "Percorso panoramico",
                      "startLocation": "Bormio",
                      "endLocation": "Prato allo Stelvio",
                      "distanceKm": 47.50,
                      "difficulty": "HARD"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andReturn();

        String responseBody = result
            .getResponse()
            .getContentAsString();

        JsonNode responseJson = objectMapper.readTree(responseBody);

        UUID routeId = UUID.fromString(
            responseJson.required("id").asString()
        );

        assertThat(result.getResponse().getHeader("Location"))
            .isEqualTo("/api/routes/" + routeId);

        Route persistedRoute = routeRepository
            .findById(routeId)
            .orElseThrow();

        assertThat(persistedRoute.getId()).isEqualTo(routeId);
        assertThat(persistedRoute.getName())
            .isEqualTo("Passo dello Stelvio");
        assertThat(persistedRoute.getDescription())
            .isEqualTo("Percorso panoramico");
        assertThat(persistedRoute.getStartLocation())
            .isEqualTo("Bormio");
        assertThat(persistedRoute.getEndLocation())
            .isEqualTo("Prato allo Stelvio");
        assertThat(persistedRoute.getDistanceKm())
            .isEqualByComparingTo("47.50");
        assertThat(persistedRoute.getDifficulty())
            .isEqualTo(Difficulty.HARD);
        assertThat(persistedRoute.getCreatedAt()).isNotNull();
        assertThat(persistedRoute.getUpdatedAt()).isNotNull();
    }
}
