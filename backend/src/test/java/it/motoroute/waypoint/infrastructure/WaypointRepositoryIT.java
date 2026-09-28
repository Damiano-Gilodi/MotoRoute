package it.motoroute.waypoint.infrastructure;

import it.motoroute.TestcontainersConfiguration;
import it.motoroute.route.domain.Difficulty;
import it.motoroute.route.domain.Route;
import it.motoroute.route.infrastructure.RouteRepository;
import it.motoroute.waypoint.domain.Waypoint;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
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
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class WaypointRepositoryIT {

    @Autowired
    private WaypointRepository waypointRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Route savedRoute;

    @BeforeEach
    void setUp() {
        Route route = Route.create(
            "Passo dello Stelvio",
            "Percorso panoramico",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD
        );

        savedRoute = routeRepository.saveAndFlush(route);

        entityManager.clear();
    }

    @Test
    void shouldSaveAndReloadWaypoint() {

        Waypoint waypoint = Waypoint.create(
            savedRoute,
            "waypoint name",
            "waypoint description",
            1,
            new BigDecimal("45.234534"),
            new BigDecimal("125.34455")
        );

        Waypoint savedWaypoint = waypointRepository.saveAndFlush(waypoint);

        entityManager.clear();

        Waypoint reloadedWaypoint = waypointRepository.findById(savedWaypoint.getId()).orElseThrow();

        assertThat(reloadedWaypoint.getId()).isEqualTo(savedWaypoint.getId());
        assertThat(reloadedWaypoint.getRouteId()).isEqualTo(savedRoute.getId());
        assertThat(reloadedWaypoint.getName()).isEqualTo("waypoint name");
        assertThat(reloadedWaypoint.getDescription()).isEqualTo("waypoint description");
        assertThat(reloadedWaypoint.getPosition()).isEqualTo(1);
        assertThat(reloadedWaypoint.getLatitude()).isEqualByComparingTo("45.234534");
        assertThat(reloadedWaypoint.getLongitude()).isEqualByComparingTo("125.34455");
        assertThat(reloadedWaypoint.getCreatedAt()).isCloseTo(savedWaypoint.getCreatedAt(), within(1, ChronoUnit.MILLIS));
        assertThat(reloadedWaypoint.getUpdatedAt()).isCloseTo(savedWaypoint.getUpdatedAt(), within(1, ChronoUnit.MILLIS));
    }

    @Test
    void shouldRejectNonPositivePositionAtDatabaseLevel() {
        assertThatThrownBy(() -> jdbcTemplate.update("""
                    INSERT INTO waypoints (
                        id,
                        route_id,
                        name,
                        description,
                        position,
                        latitude,
                        longitude,
                        created_at,
                        updated_at
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """,
            UUID.randomUUID(),
            savedRoute.getId(),
            "waypoint name",
            "waypoint description",
            -1,
            new BigDecimal("45.234534"),
            new BigDecimal("125.34455")
        )).isInstanceOf(DataIntegrityViolationException.class)
            .hasMessageContaining("chk_waypoints_position");
    }

    @Test
    void shouldRejectInvalidLatitudeAtDatabaseLevel() {
        assertThatThrownBy(() -> jdbcTemplate.update("""
                    INSERT INTO waypoints (
                        id,
                        route_id,
                        name,
                        description,
                        position,
                        latitude,
                        longitude,
                        created_at,
                        updated_at
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """,
            UUID.randomUUID(),
            savedRoute.getId(),
            "waypoint name",
            "waypoint description",
            1,
            new BigDecimal("-90.234534"),
            new BigDecimal("125.34455")
        )).isInstanceOf(DataIntegrityViolationException.class)
            .hasMessageContaining("chk_waypoints_latitude");
    }

    @Test
    void shouldRejectInvalidLongitudeAtDatabaseLevel() {
        assertThatThrownBy(() -> jdbcTemplate.update("""
                    INSERT INTO waypoints (
                        id,
                        route_id,
                        name,
                        description,
                        position,
                        latitude,
                        longitude,
                        created_at,
                        updated_at
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """,
            UUID.randomUUID(),
            savedRoute.getId(),
            "waypoint name",
            "waypoint description",
            1,
            new BigDecimal("-45.234534"),
            new BigDecimal("180.34455")
        )).isInstanceOf(DataIntegrityViolationException.class)
            .hasMessageContaining("chk_waypoints_longitude");
    }

    @Test
    void shouldRejectInvalidRouteIdAtDatabaseLevel() {
        assertThatThrownBy(() -> jdbcTemplate.update("""
                    INSERT INTO waypoints (
                        id,
                        route_id,
                        name,
                        description,
                        position,
                        latitude,
                        longitude,
                        created_at,
                        updated_at
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """,
            UUID.randomUUID(),
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            "waypoint name",
            "waypoint description",
            1,
            new BigDecimal("-45.234534"),
            new BigDecimal("52.34455")
        )).isInstanceOf(DataIntegrityViolationException.class)
            .hasMessageContaining("fk_waypoints_route");
    }

    @Test
    void shouldRejectNonUniqueRouteIdPositionAtDatabaseLevel() {

        Waypoint waypoint = Waypoint.create(
            savedRoute,
            "waypoint name",
            "waypoint description",
            1,
            new BigDecimal("45.234534"),
            new BigDecimal("125.34455")
        );

        waypointRepository.saveAndFlush(waypoint);

        assertThatThrownBy(() -> jdbcTemplate.update("""
                    INSERT INTO waypoints (
                        id,
                        route_id,
                        name,
                        description,
                        position,
                        latitude,
                        longitude,
                        created_at,
                        updated_at
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """,
            UUID.randomUUID(),
            savedRoute.getId(),
            "waypoint name",
            "waypoint description",
            1,
            new BigDecimal("-45.234534"),
            new BigDecimal("45.34455")
        )).isInstanceOf(DataIntegrityViolationException.class)
            .hasMessageContaining("uk_waypoints_route_position");
    }

    @Test
    void shouldDeleteCascadeWaypoint() {

        Waypoint waypoint = Waypoint.create(
            savedRoute,
            "waypoint name",
            "waypoint description",
            1,
            new BigDecimal("45.234534"),
            new BigDecimal("125.34455")
        );

        waypointRepository.saveAndFlush(waypoint);

        UUID waypointId = waypoint.getId();
        UUID routeId = savedRoute.getId();

        routeRepository.delete(savedRoute);

        entityManager.flush();
        entityManager.clear();

        assertThat(routeRepository.findById(routeId)).isEmpty();
        assertThat(waypointRepository.findById(waypointId)).isEmpty();
    }

    @Test
    void shouldAllowSamePositionForDifferentRoutes() {

        Route route2 = Route.create(
            "Lanzada",
            "Percorso panoramico",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD
        );
        Route savedRoute2 = routeRepository.saveAndFlush(route2);

        Waypoint waypoint1 = Waypoint.create(
            savedRoute,
            "waypoint1 name",
            "waypoint description",
            1,
            new BigDecimal("45.234534"),
            new BigDecimal("125.34455")
        );
        waypointRepository.saveAndFlush(waypoint1);

        Waypoint waypoint2 = Waypoint.create(
            savedRoute2,
            "waypoint2 name",
            "waypoint description",
            1,
            new BigDecimal("45.234534"),
            new BigDecimal("125.34455")
        );
        waypointRepository.saveAndFlush(waypoint2);

        entityManager.clear();

        Waypoint reloadedWaypoint1 = waypointRepository.findById(waypoint1.getId()).orElseThrow();
        Waypoint reloadedWaypoint2 = waypointRepository.findById(waypoint2.getId()).orElseThrow();

        assertThat(reloadedWaypoint1.getRouteId()).isEqualTo(savedRoute.getId());
        assertThat(reloadedWaypoint2.getRouteId()).isEqualTo(savedRoute2.getId());
        assertThat(reloadedWaypoint1.getPosition()).isEqualTo(1);
        assertThat(reloadedWaypoint2.getPosition()).isEqualTo(1);
    }

    @Test
    void shouldListWaypointsOrderedByPosition() {
        Waypoint waypoint1 = Waypoint.create(
            savedRoute,
            "name1",
            "waypoint description",
            1,
            new BigDecimal("45.234534"),
            new BigDecimal("125.34455")
        );

        Waypoint waypoint2 = Waypoint.create(
            savedRoute,
            "name2",
            null,
            2,
            new BigDecimal("45.234534"),
            new BigDecimal("125.34455")
        );

        waypointRepository.saveAndFlush(waypoint2);
        waypointRepository.saveAndFlush(waypoint1);

        entityManager.clear();

        Page<Waypoint> waypointPage = waypointRepository.findAllByRoute_Id(savedRoute.getId(), PageRequest.of(0, 10, Sort.by("position")));

        assertThat(waypointPage.getContent()).hasSize(2);
        assertThat(waypointPage.getContent()).extracting(Waypoint::getPosition).containsExactly(1, 2);
        assertThat(waypointPage.getContent()).extracting(Waypoint::getName).containsExactly("name1", "name2");
        assertThat(waypointPage.getNumber()).isEqualTo(0);
        assertThat(waypointPage.getSize()).isEqualTo(10);
        assertThat(waypointPage.getTotalElements()).isEqualTo(2);
        assertThat(waypointPage.getTotalPages()).isEqualTo(1);
        assertThat(waypointPage.isFirst()).isTrue();
        assertThat(waypointPage.isLast()).isTrue();
    }
}
