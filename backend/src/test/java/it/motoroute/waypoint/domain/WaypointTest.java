package it.motoroute.waypoint.domain;

import it.motoroute.route.domain.Route;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class WaypointTest {

    private final Route route = mock(Route.class);

    @Test
    void shouldCreateValidWaypoint() {

        Waypoint waypoint = Waypoint.create(
            route,
            "Punto di interesse",
            "Un punto di riferimento",
            1,
            new BigDecimal("45.464231"),
            new BigDecimal("150.191383")
        );

        assertThat(waypoint.getId()).isNotNull();
        assertThat(waypoint.getRouteId()).isEqualTo(route.getId());
        assertThat(waypoint.getName()).isEqualTo("Punto di interesse");
        assertThat(waypoint.getDescription()).isEqualTo("Un punto di riferimento");
        assertThat(waypoint.getPosition()).isEqualTo(1);
        assertThat(waypoint.getLatitude()).isEqualByComparingTo("45.464231");
        assertThat(waypoint.getLongitude()).isEqualByComparingTo("150.191383");
        assertThat(waypoint.getCreatedAt()).isNotNull();
        assertThat(waypoint.getUpdatedAt()).isEqualTo(waypoint.getCreatedAt());
    }

    @Test
    void shouldRejectNullRoute() {
        assertThatThrownBy(() -> Waypoint.create(
            null,
            "Punto di interesse",
            "Un punto di riferimento",
            1,
            new BigDecimal("45.464231"),
            new BigDecimal("150.191383")
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("route must not be null");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectInvalidName(String name) {
        assertThatThrownBy(() -> Waypoint.create(
            route,
            name,
            "Un punto di riferimento",
            1,
            new BigDecimal("45.464231"),
            new BigDecimal("150.191383")
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("name must not be blank");
    }

    @Test
    void shouldRejectNameExceedingMaxLength() {
        String name = "a".repeat(121);

        assertThatThrownBy(() -> Waypoint.create(
            route,
            name,
            null,
            1,
            new BigDecimal("45.464231"),
            new BigDecimal("150.191383")
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("name must not exceed 120 characters");
    }

    @Test
    void shouldNormalizeTextFields() {
        Waypoint waypoint = Waypoint.create(
            route,
            "   Punto di interesse   ",
            "   Un punto di riferimento   ",
            1,
            new BigDecimal("45.464231"),
            new BigDecimal("150.191383")
        );

        assertThat(waypoint.getName()).isEqualTo("Punto di interesse");
        assertThat(waypoint.getDescription()).isEqualTo("Un punto di riferimento");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -150})
    void shouldRejectNonPositivePosition(Integer position) {
        assertThatThrownBy(() -> Waypoint.create(
            route,
            "Punto di interesse",
            "Un punto di riferimento",
            position,
            new BigDecimal("45.464231"),
            new BigDecimal("150.191383")
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("position must be greater than zero");
    }

    @Test
    void shouldRejectNullPosition() {
        assertThatThrownBy(() -> Waypoint.create(
            route,
            "Punto di interesse",
            "Un punto di riferimento",
            null,
            new BigDecimal("45.464231"),
            new BigDecimal("150.191383")
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("position must be greater than zero");
    }

    @ParameterizedTest
    @ValueSource(strings = {"95.123456", "-95.654321"})
    void shouldRejectInvalidLatitudeLimit(String latitude) {
        assertThatThrownBy(() -> Waypoint.create(
            route,
            "Punto di interesse",
            null,
            1,
            new BigDecimal(latitude),
            new BigDecimal("150.191383")
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("latitude must be between -90 and 90");
    }

    @ParameterizedTest
    @ValueSource(strings = {"89.1234567", "-25.6543215"})
    void shouldRejectInvalidLatitudeScale(String latitude) {
        assertThatThrownBy(() -> Waypoint.create(
            route,
            "Punto di interesse",
            null,
            1,
            new BigDecimal(latitude),
            new BigDecimal("150.191383")
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("latitude must not exceed 6 decimal places");
    }

    @Test
    void shouldRejectNullLatitude() {
        assertThatThrownBy(() -> Waypoint.create(
            route,
            "Punto di interesse",
            "Un punto di riferimento",
            1,
            null,
            new BigDecimal("150.191383")
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("latitude must be between -90 and 90");
    }

    @ParameterizedTest
    @ValueSource(strings = {"180.123456", "-180.654321"})
    void shouldRejectInvalidLongitudeLimit(String longitude) {
        assertThatThrownBy(() -> Waypoint.create(
            route,
            "Punto di interesse",
            null,
            1,
            new BigDecimal("45.464231"),
            new BigDecimal(longitude)
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("longitude must be between -180 and 180");
    }

    @ParameterizedTest
    @ValueSource(strings = {"179.1234565", "-34.65432134"})
    void shouldRejectInvalidLongitudeScale(String longitude) {
        assertThatThrownBy(() -> Waypoint.create(
            route,
            "Punto di interesse",
            null,
            1,
            new BigDecimal("45.464231"),
            new BigDecimal(longitude)
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("longitude must not exceed 6 decimal places");
    }

    @Test
    void shouldRejectNullLongitude() {
        assertThatThrownBy(() -> Waypoint.create(
            route,
            "Punto di interesse",
            "Un punto di riferimento",
            1,
            new BigDecimal("45.464231"),
            null
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("longitude must be between -180 and 180");
    }
}
