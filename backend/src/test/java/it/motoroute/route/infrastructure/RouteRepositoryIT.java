package it.motoroute.route.infrastructure;

import it.motoroute.TestcontainersConfiguration;
import it.motoroute.route.domain.Difficulty;
import it.motoroute.route.domain.Route;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
class RouteRepositoryIT {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldSaveAndReloadRoute() {
        Route route = Route.create(
            "Passo dello Stelvio",
            "Percorso panoramico",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD
        );

        Route savedRoute = routeRepository.saveAndFlush(route);

        entityManager.clear();

        Route reloadedRoute = routeRepository
            .findById(savedRoute.getId())
            .orElseThrow();

        assertThat(reloadedRoute.getId()).isEqualTo(savedRoute.getId());
        assertThat(reloadedRoute.getName()).isEqualTo("Passo dello Stelvio");
        assertThat(reloadedRoute.getDescription())
            .isEqualTo("Percorso panoramico");
        assertThat(reloadedRoute.getStartLocation()).isEqualTo("Bormio");
        assertThat(reloadedRoute.getEndLocation())
            .isEqualTo("Prato allo Stelvio");
        assertThat(reloadedRoute.getDistanceKm())
            .isEqualByComparingTo("47.50");
        assertThat(reloadedRoute.getDifficulty())
            .isEqualTo(Difficulty.HARD);
        assertThat(reloadedRoute.getCreatedAt())
            .isCloseTo(savedRoute.getCreatedAt(), within(1, ChronoUnit.MILLIS));
        assertThat(reloadedRoute.getUpdatedAt())
            .isCloseTo(savedRoute.getUpdatedAt(), within(1, ChronoUnit.MILLIS));
    }

    @Test
    void shouldRejectNonPositiveDistanceAtDatabaseLevel() {
        assertThatThrownBy(() -> jdbcTemplate.update("""
            INSERT INTO routes (
                id,
                name,
                description,
                start_location,
                end_location,
                distance_km,
                difficulty,
                created_at,
                updated_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """,
            UUID.randomUUID(),
            "Percorso non valido",
            null,
            "Bormio",
            "Prato allo Stelvio",
            BigDecimal.ZERO,
            "EASY"
        )).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRejectInvalidDifficultyAtDatabaseLevel() {
        assertThatThrownBy(() -> jdbcTemplate.update("""
            INSERT INTO routes (
                id,
                name,
                start_location,
                end_location,
                distance_km,
                difficulty,
                created_at,
                updated_at
            )
            VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """,
            UUID.randomUUID(),
            "Percorso",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            "INVALID"
        )).isInstanceOf(DataIntegrityViolationException.class);
    }
}
