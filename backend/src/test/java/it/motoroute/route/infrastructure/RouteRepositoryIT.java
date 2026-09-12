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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
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

    @Test
    void shouldReturnRequestedPageSortedByNameAscending() {
        List<Route> routes = List.of(
            createRoute("Alpi"),
            createRoute("Dolomiti"),
            createRoute("Garda"),
            createRoute("Stelvio"),
            createRoute("Valle d'Aosta")
        );

        routeRepository.saveAllAndFlush(routes);

        entityManager.clear();

        Page<Route> routePage = routeRepository.findAll(
            PageRequest.of(
                1,
                2,
                Sort.by(
                    Sort.Order.asc("name")
                )
            )
        );

        assertThat(routePage.getNumber()).isEqualTo(1);
        assertThat(routePage.getSize()).isEqualTo(2);
        assertThat(routePage.getNumberOfElements()).isEqualTo(2);
        assertThat(routePage.getTotalElements()).isEqualTo(5);
        assertThat(routePage.getTotalPages()).isEqualTo(3);
        assertThat(routePage.isFirst()).isFalse();
        assertThat(routePage.isLast()).isFalse();

        assertThat(routePage.getContent())
            .extracting(Route::getName)
            .containsExactly(
                "Garda",
                "Stelvio"
            );
    }

    @Test
    void shouldReturnRoutesSortedByCreatedAtDescending() {
        insertRoute(
            "Oldest route",
            OffsetDateTime.parse("2026-07-27T08:00:00Z")
        );

        insertRoute(
            "Newest route",
            OffsetDateTime.parse("2026-07-27T10:00:00Z")
        );

        insertRoute(
            "Middle route",
            OffsetDateTime.parse("2026-07-27T09:00:00Z")
        );

        entityManager.clear();

        Page<Route> routePage = routeRepository.findAll(
            PageRequest.of(
                0,
                20,
                Sort.by(
                    Sort.Order.desc("createdAt")
                )
            )
        );

        assertThat(routePage.getTotalElements()).isEqualTo(3);

        assertThat(routePage.getContent())
            .extracting(Route::getName)
            .containsExactly(
                "Newest route",
                "Middle route",
                "Oldest route"
            );
    }

    private Route createRoute(String name) {
        return Route.create(
            name,
            "Route description",
            "Start location",
            "End location",
            new BigDecimal("50.00"),
            Difficulty.MEDIUM
        );
    }

    private void insertRoute(
        String name,
        OffsetDateTime createdAt
    ) {
        jdbcTemplate.update("""
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
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
            UUID.randomUUID(),
            name,
            "Route description",
            "Start location",
            "End location",
            new BigDecimal("50.00"),
            "MEDIUM",
            createdAt,
            createdAt
        );
    }
}
