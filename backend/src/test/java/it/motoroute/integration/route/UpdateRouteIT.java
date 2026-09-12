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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class UpdateRouteIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RouteRepository routeRepository;

    @Test
    void shouldUpdateRouteAndPersistChanges() throws Exception {
        Route route = Route.create(
            "Passo dello Stelvio",
            "Percorso panoramico",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD
        );

        UUID routeId = route.getId();

        routeRepository.saveAndFlush(route);

        Route persistedBeforeUpdate =
            routeRepository.findById(routeId)
                .orElseThrow();

        OffsetDateTime originalCreatedAt =
            persistedBeforeUpdate.getCreatedAt();

        OffsetDateTime originalUpdatedAt =
            persistedBeforeUpdate.getUpdatedAt();

        mockMvc.perform(
                put("/api/routes/{routeId}", routeId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "name": "Passo dello Stelvio aggiornato",
                          "description": "  Percorso culturale  ",
                          "startLocation": "  Bormio  ",
                          "endLocation": "  Prato allo Stelvio  ",
                          "distanceKm": 40.00,
                          "difficulty": "MEDIUM"
                        }
                        """)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(
                MediaType.APPLICATION_JSON
            ))
            .andExpect(jsonPath("$.id")
                .value(routeId.toString()))
            .andExpect(jsonPath("$.name")
                .value("Passo dello Stelvio aggiornato"))
            .andExpect(jsonPath("$.description")
                .value("Percorso culturale"))
            .andExpect(jsonPath("$.startLocation")
                .value("Bormio"))
            .andExpect(jsonPath("$.endLocation")
                .value("Prato allo Stelvio"))
            .andExpect(jsonPath("$.distanceKm")
                .value(40.00))
            .andExpect(jsonPath("$.difficulty")
                .value("MEDIUM"));

        Route persistedAfterUpdate =
            routeRepository.findById(routeId)
                .orElseThrow();

        assertThat(persistedAfterUpdate.getId())
            .isEqualTo(routeId);

        assertThat(persistedAfterUpdate.getName())
            .isEqualTo("Passo dello Stelvio aggiornato");

        assertThat(persistedAfterUpdate.getDescription())
            .isEqualTo("Percorso culturale");

        assertThat(persistedAfterUpdate.getStartLocation())
            .isEqualTo("Bormio");

        assertThat(persistedAfterUpdate.getEndLocation())
            .isEqualTo("Prato allo Stelvio");

        assertThat(persistedAfterUpdate.getDistanceKm())
            .isEqualByComparingTo("40.00");

        assertThat(persistedAfterUpdate.getDifficulty())
            .isEqualTo(Difficulty.MEDIUM);

        assertThat(persistedAfterUpdate.getCreatedAt())
            .isEqualTo(originalCreatedAt);

        assertThat(persistedAfterUpdate.getUpdatedAt())
            .isAfter(originalUpdatedAt);
    }
}
