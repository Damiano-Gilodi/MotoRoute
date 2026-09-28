package it.motoroute.integration.waypoint;

import it.motoroute.TestcontainersConfiguration;
import it.motoroute.route.domain.Difficulty;
import it.motoroute.route.domain.Route;
import it.motoroute.route.infrastructure.RouteRepository;
import it.motoroute.waypoint.api.WaypointPageResponse;
import it.motoroute.waypoint.api.WaypointSummaryResponse;
import it.motoroute.waypoint.domain.Waypoint;
import it.motoroute.waypoint.infrastructure.WaypointRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class ListWaypointsIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private WaypointRepository waypointRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID createdRouteId;

    @AfterEach
    void tearDown() {
        if (createdRouteId != null) {
            routeRepository.deleteById(createdRouteId);
        }
    }

    @Test
    void shouldListWaypointsOfRoute() throws Exception {

        Route route = Route.create(
            "Passo dello Stelvio",
            "Percorso panoramico",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD
        );

        Route savedRoute = routeRepository.saveAndFlush(route);
        createdRouteId = savedRoute.getId();

        Waypoint waypoint1 = Waypoint.create(
            route,
            "waypoint1",
            null,
            1,
            new BigDecimal("45.123456"),
            new BigDecimal("8.765432")
        );

        Waypoint waypoint2 = Waypoint.create(
            route,
            "waypoint2",
            null,
            2,
            new BigDecimal("45.123456"),
            new BigDecimal("8.765432")
        );

        waypointRepository.saveAndFlush(waypoint2);
        waypointRepository.saveAndFlush(waypoint1);

        MvcResult result = mockMvc.perform(get("/api/routes/{routeId}/waypoints", createdRouteId))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        WaypointPageResponse response = objectMapper.readValue(responseBody, WaypointPageResponse.class);

        assertThat(response.content()).hasSize(2);
        assertThat(response.content()).extracting(WaypointSummaryResponse::name).containsExactly("waypoint1", "waypoint2");
        assertThat(response.content()).extracting(WaypointSummaryResponse::position).containsExactly(1, 2);
        assertThat(response.page()).isEqualTo(0);
        assertThat(response.size()).isEqualTo(20);
        assertThat(response.totalElements()).isEqualTo(2);
        assertThat(response.totalPages()).isEqualTo(1);
        assertThat(response.first()).isTrue();
        assertThat(response.last()).isTrue();

        mockMvc.perform(get("/api/routes/{routeId}/waypoints", createdRouteId)
                .param("page", "0")
                .param("size", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].name").value("waypoint1"))
            .andExpect(jsonPath("$.content[0].position").value(1))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(1))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.first").value(true))
            .andExpect(jsonPath("$.last").value(false));

        mockMvc.perform(get("/api/routes/{routeId}/waypoints", createdRouteId)
                .param("page", "1")
                .param("size", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].name").value("waypoint2"))
            .andExpect(jsonPath("$.content[0].position").value(2))
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.size").value(1))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.first").value(false))
            .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void shouldReturn404WhenRouteDoesNotExist() throws Exception {
        UUID unknownRouteId = UUID.randomUUID();

        mockMvc.perform(get("/api/routes/{routeId}/waypoints", unknownRouteId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message")
                .value("Route not found with id: " + unknownRouteId))
            .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }
}
