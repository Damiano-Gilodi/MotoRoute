package it.motoroute.route.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RouteTest {

    @Test
    void shouldCreateValidRoute() {
        Route route = Route.create(
            "Passo dello Stelvio",
            "Percorso panoramico",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD
        );

        assertThat(route.getId()).isNotNull();
        assertThat(route.getName()).isEqualTo("Passo dello Stelvio");
        assertThat(route.getDescription()).isEqualTo("Percorso panoramico");
        assertThat(route.getStartLocation()).isEqualTo("Bormio");
        assertThat(route.getEndLocation()).isEqualTo("Prato allo Stelvio");
        assertThat(route.getDistanceKm()).isEqualByComparingTo("47.50");
        assertThat(route.getDifficulty()).isEqualTo(Difficulty.HARD);
        assertThat(route.getCreatedAt()).isNotNull();
        assertThat(route.getUpdatedAt()).isEqualTo(route.getCreatedAt());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectInvalidName(String name) {
        assertThatThrownBy(() -> Route.create(
            name,
            "Percorso panoramico",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("name must not be blank");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectInvalidStartLocation(String startLocation) {
        assertThatThrownBy(() -> Route.create(
            "Passo dello Stelvio",
            null,
            startLocation,
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("startLocation must not be blank");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectInvalidEndLocation(String endLocation) {
        assertThatThrownBy(() -> Route.create(
            "Passo dello Stelvio",
            null,
            "Bormio",
            endLocation,
            new BigDecimal("47.50"),
            Difficulty.HARD
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("endLocation must not be blank");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "-1.50"})
    void shouldRejectNonPositiveDistance(String distanceKm) {
        assertThatThrownBy(() -> Route.create(
            "Passo dello Stelvio",
            null,
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal(distanceKm),
            Difficulty.HARD
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("distanceKm must be greater than zero");
    }

    @Test
    void shouldRejectNullDistance() {
        assertThatThrownBy(() -> Route.create(
            "Passo dello Stelvio",
            null,
            "Bormio",
            "Prato allo Stelvio",
            null,
            Difficulty.HARD
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("distanceKm must be greater than zero");
    }

    @Test
    void shouldRejectNullDifficulty() {
        assertThatThrownBy(() -> Route.create(
            "Passo dello Stelvio",
            null,
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            null
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("difficulty must not be null");
    }

    @Test
    void shouldNormalizeTextFields() {
        Route route = Route.create(
            "  Passo dello Stelvio  ",
            "  Percorso panoramico  ",
            "  Bormio  ",
            "  Prato allo Stelvio  ",
            new BigDecimal("47.50"),
            Difficulty.HARD
        );

        assertThat(route.getName()).isEqualTo("Passo dello Stelvio");
        assertThat(route.getDescription()).isEqualTo("Percorso panoramico");
        assertThat(route.getStartLocation()).isEqualTo("Bormio");
        assertThat(route.getEndLocation()).isEqualTo("Prato allo Stelvio");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldConvertMissingDescriptionToNull(String description) {
        Route route = Route.create(
            "Passo dello Stelvio",
            description,
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD
        );

        assertThat(route.getDescription()).isNull();
    }

    @Test
    void shouldRejectNameLongerThan120Characters() {
        assertThatThrownBy(() ->
            createRoute(
                "a".repeat(121),
                "Description",
                "Start location",
                "End location"
            )
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(
                "name must not exceed 120 characters"
            );
    }

    @Test
    void shouldRejectDescriptionLongerThan2000Characters() {
        assertThatThrownBy(() ->
            createRoute(
                "Valid route",
                "a".repeat(2001),
                "Start location",
                "End location"
            )
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(
                "description must not exceed 2000 characters"
            );
    }

    @Test
    void shouldRejectStartLocationLongerThan120Characters() {
        assertThatThrownBy(() ->
            createRoute(
                "Valid route",
                "Description",
                "a".repeat(121),
                "End location"
            )
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(
                "startLocation must not exceed 120 characters"
            );
    }

    @Test
    void shouldRejectEndLocationLongerThan120Characters() {
        assertThatThrownBy(() ->
            createRoute(
                "Valid route",
                "Description",
                "Start location",
                "a".repeat(121)
            )
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(
                "endLocation must not exceed 120 characters"
            );
    }

    @Test
    void shouldAcceptMaximumAllowedLengths() {
        Route route = createRoute(
            "a".repeat(120),
            "b".repeat(2000),
            "c".repeat(120),
            "d".repeat(120)
        );

        assertThat(route.getName()).hasSize(120);
        assertThat(route.getDescription()).hasSize(2000);
        assertThat(route.getStartLocation()).hasSize(120);
        assertThat(route.getEndLocation()).hasSize(120);
    }

    private Route createRoute(
        String name,
        String description,
        String startLocation,
        String endLocation
    ) {
        return Route.create(
            name,
            description,
            startLocation,
            endLocation,
            new BigDecimal("50.00"),
            Difficulty.MEDIUM
        );
    }
}
