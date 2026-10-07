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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class UpdateWaypointIT {

    private static final String VALID_UPDATE_JSON = """
        {
          "name": "Punto modificato",
          "description": "Descrizione modificata",
          "position": 2,
          "latitude": 44.123456,
          "longitude": 11.654321
        }
        """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private WaypointRepository waypointRepository;

    private final List<UUID> createdRouteIds = new ArrayList<>();

    @AfterEach
    void tearDown() {
        for (UUID routeId : createdRouteIds) {
            routeRepository.deleteById(routeId);
        }
        createdRouteIds.clear();
    }

    @Test
    void shouldUpdateWaypointAndPersistChanges() throws Exception {

        Route route = Route.create(
            "Passo dello Stelvio",
            "Percorso panoramico",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD
        );

        Route savedRoute = routeRepository.saveAndFlush(route);
        UUID routeId = savedRoute.getId();
        createdRouteIds.add(routeId);

        Waypoint waypoint = Waypoint.create(
            savedRoute,
            "Punto originale",
            "Descrizione originale",
            1,
            new BigDecimal("45.123456"),
            new BigDecimal("8.765432")
        );

        Waypoint savedWaypoint = waypointRepository.saveAndFlush(waypoint);
        UUID waypointId = savedWaypoint.getId();

        Waypoint persistedBeforeUpdate = waypointRepository.findById(waypointId)
            .orElseThrow();

        OffsetDateTime createdAtBeforeUpdate = persistedBeforeUpdate.getCreatedAt();
        OffsetDateTime updatedAtBeforeUpdate = persistedBeforeUpdate.getUpdatedAt();

        mockMvc.perform(put("/api/routes/{routeId}/waypoints/{waypointId}", routeId, waypointId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_UPDATE_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(waypointId.toString()))
            .andExpect(jsonPath("$.routeId").value(routeId.toString()))
            .andExpect(jsonPath("$.name").value("Punto modificato"))
            .andExpect(jsonPath("$.description").value("Descrizione modificata"))
            .andExpect(jsonPath("$.position").value(2))
            .andExpect(jsonPath("$.latitude").value(44.123456))
            .andExpect(jsonPath("$.longitude").value(11.654321));

        Waypoint persistedAfterUpdate = waypointRepository.findById(waypointId)
            .orElseThrow();

        assertThat(persistedAfterUpdate.getName()).isEqualTo("Punto modificato");
        assertThat(persistedAfterUpdate.getDescription()).isEqualTo("Descrizione modificata");
        assertThat(persistedAfterUpdate.getPosition()).isEqualTo(2);
        assertThat(persistedAfterUpdate.getLatitude()).isEqualByComparingTo("44.123456");
        assertThat(persistedAfterUpdate.getLongitude()).isEqualByComparingTo("11.654321");
        assertThat(persistedAfterUpdate.getCreatedAt()).isEqualTo(createdAtBeforeUpdate);
        assertThat(persistedAfterUpdate.getUpdatedAt()).isAfterOrEqualTo(updatedAtBeforeUpdate);
    }

    @Test
    void shouldNotUpdateWaypointBelongingToAnotherRoute() throws Exception {

        Route routeA = Route.create(
            "Route A",
            null,
            "Bologna",
            "Firenze",
            new BigDecimal("120.00"),
            Difficulty.MEDIUM
        );

        Route routeB = Route.create(
            "Route B",
            null,
            "Milano",
            "Torino",
            new BigDecimal("140.00"),
            Difficulty.MEDIUM
        );

        Route savedRouteA = routeRepository.saveAndFlush(routeA);
        Route savedRouteB = routeRepository.saveAndFlush(routeB);

        createdRouteIds.add(savedRouteA.getId());
        createdRouteIds.add(savedRouteB.getId());

        Waypoint waypoint = Waypoint.create(
            savedRouteB,
            "Punto originale",
            "Descrizione originale",
            1,
            new BigDecimal("45.123456"),
            new BigDecimal("8.765432")
        );

        Waypoint savedWaypoint = waypointRepository.saveAndFlush(waypoint);

        mockMvc.perform(put("/api/routes/{routeId}/waypoints/{waypointId}", savedRouteA.getId(), savedWaypoint.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_UPDATE_JSON))
            .andExpect(status().isNotFound());

        Waypoint persistedWaypoint = waypointRepository.findById(savedWaypoint.getId())
            .orElseThrow();

        assertThat(persistedWaypoint.getName()).isEqualTo("Punto originale");
        assertThat(persistedWaypoint.getDescription()).isEqualTo("Descrizione originale");
        assertThat(persistedWaypoint.getPosition()).isEqualTo(1);
        assertThat(persistedWaypoint.getRouteId()).isEqualTo(savedRouteB.getId());
    }
}
