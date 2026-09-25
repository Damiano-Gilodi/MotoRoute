package it.motoroute.integration.waypoint;

import it.motoroute.TestcontainersConfiguration;
import it.motoroute.route.domain.Difficulty;
import it.motoroute.route.domain.Route;
import it.motoroute.route.infrastructure.RouteRepository;
import it.motoroute.waypoint.domain.Waypoint;
import it.motoroute.waypoint.infrastructure.WaypointRepository;
import org.junit.jupiter.api.AfterEach;
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

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class CreateWaypointIT {

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
    void shouldCreateAndPersistWaypoint() throws Exception {

        Route route = Route.create(
            "Passo dello Stelvio",
            "Percorso panoramico",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD
        );

        createdRouteId = route.getId();

        routeRepository.saveAndFlush(route);

        MvcResult result = mockMvc.perform(post("/api/routes/{routeId}/waypoints", createdRouteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Punto panoramico",
                      "description": "Belvedere con vista sulle montagne",
                      "position": 1,
                      "latitude": 45.123456,
                      "longitude": 8.765432
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode responseJson = objectMapper.readTree(responseBody);
        UUID waypointId = UUID.fromString(responseJson.required("id").asString());

        assertThat(result.getResponse().getHeader("Location"))
            .isEqualTo("/api/routes/" + createdRouteId + "/waypoints/" + waypointId);

        Waypoint persistedWaypoint = waypointRepository.findById(waypointId).orElseThrow();

        assertThat(persistedWaypoint.getId()).isEqualTo(waypointId);
        assertThat(persistedWaypoint.getRouteId()).isEqualTo(createdRouteId);
        assertThat(persistedWaypoint.getName()).isEqualTo("Punto panoramico");
        assertThat(persistedWaypoint.getDescription()).isEqualTo("Belvedere con vista sulle montagne");
        assertThat(persistedWaypoint.getPosition()).isEqualTo(1);
        assertThat(persistedWaypoint.getLatitude()).isEqualByComparingTo("45.123456");
        assertThat(persistedWaypoint.getLongitude()).isEqualByComparingTo("8.765432");
        assertThat(persistedWaypoint.getCreatedAt()).isNotNull();
        assertThat(persistedWaypoint.getUpdatedAt()).isNotNull();
        assertThat(persistedWaypoint.getUpdatedAt()).isEqualTo(persistedWaypoint.getCreatedAt());
    }

    @Test
    void shouldReturn409WhenPositionAlreadyExists() throws Exception {

        Route route = Route.create(
            "Passo dello Stelvio",
            "Percorso panoramico",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD
        );

        createdRouteId = route.getId();

        routeRepository.saveAndFlush(route);

        Waypoint waypoint = Waypoint.create(
            route,
            "waypoint1",
            null,
            1,
            new BigDecimal("45.123456"),
            new BigDecimal("8.765432")
        );

        waypointRepository.saveAndFlush(waypoint);

        mockMvc.perform(post("/api/routes/{routeId}/waypoints", createdRouteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "waypoint2",
                      "description": "Belvedere con vista sulle montagne",
                      "position": 1,
                      "latitude": 45.123456,
                      "longitude": 8.765432
                    }
                    """))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.error").value("Conflict"))
            .andExpect(jsonPath("$.message").value("Waypoint position " + 1 + " already exists in route: " + createdRouteId))
            .andExpect(jsonPath("$.path").value("/api/routes/" + createdRouteId + "/waypoints"))
            .andExpect(jsonPath("$.fieldErrors").isEmpty());

        assertThat(waypointRepository.count()).isEqualTo(1);
    }
}
