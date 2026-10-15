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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class DeleteWaypointIT {

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
    void shouldDeleteWaypointAndKeepRemainingSequenceStable() throws Exception {
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

        Waypoint first = waypointRepository.saveAndFlush(Waypoint.create(
            savedRoute,
            "Primo waypoint",
            null,
            1,
            new BigDecimal("45.123456"),
            new BigDecimal("8.765432")
        ));

        Waypoint middle = waypointRepository.saveAndFlush(Waypoint.create(
            savedRoute,
            "Secondo waypoint",
            null,
            2,
            new BigDecimal("45.223456"),
            new BigDecimal("8.865432")
        ));

        Waypoint last = waypointRepository.saveAndFlush(Waypoint.create(
            savedRoute,
            "Terzo waypoint",
            null,
            3,
            new BigDecimal("45.323456"),
            new BigDecimal("8.965432")
        ));

        mockMvc.perform(delete("/api/routes/{routeId}/waypoints/{waypointId}", routeId, middle.getId()))
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));

        assertThat(waypointRepository.findById(middle.getId())).isEmpty();
        assertThat(waypointRepository.findById(first.getId())).isPresent();
        assertThat(waypointRepository.findById(last.getId())).isPresent();

        Page<Waypoint> remaining = waypointRepository.findAllByRoute_Id(
            routeId,
            PageRequest.of(0, 10, Sort.by("position"))
        );

        assertThat(remaining.getContent())
            .extracting(Waypoint::getPosition)
            .containsExactly(1, 3);
    }
}
