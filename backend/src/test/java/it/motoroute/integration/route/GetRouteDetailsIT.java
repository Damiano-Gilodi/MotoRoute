package it.motoroute.integration.route;

import it.motoroute.TestcontainersConfiguration;
import it.motoroute.route.api.RouteResponse;
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
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class GetRouteDetailsIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RouteRepository routeRepository;

    @Test
    void shouldGetPersistedRouteDetails() throws Exception {

        Route route = Route.create(
            "Passo dello Stelvio",
            "Percorso panoramico",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD
        );

        Route persistedRoute = routeRepository.saveAndFlush(route);
        UUID routeId = persistedRoute.getId();

        MvcResult result = mockMvc.perform(
                get("/api/routes/" + routeId)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(
                MediaType.APPLICATION_JSON
            ))
            .andReturn();

        String responseBody = result
            .getResponse()
            .getContentAsString();

        RouteResponse response = objectMapper.readValue(
            responseBody,
            RouteResponse.class
        );

        assertThat(response.id()).isEqualTo(routeId);
        assertThat(response.name())
            .isEqualTo(persistedRoute.getName());
        assertThat(response.description())
            .isEqualTo(persistedRoute.getDescription());
        assertThat(response.startLocation())
            .isEqualTo(persistedRoute.getStartLocation());
        assertThat(response.endLocation())
            .isEqualTo(persistedRoute.getEndLocation());
        assertThat(response.distanceKm())
            .isEqualByComparingTo(persistedRoute.getDistanceKm());
        assertThat(response.difficulty())
            .isEqualTo(persistedRoute.getDifficulty());
        assertThat(response.createdAt()).isNotNull();
        assertThat(response.updatedAt()).isNotNull();
        assertThat(response.updatedAt())
            .isEqualTo(response.createdAt());
    }
}
