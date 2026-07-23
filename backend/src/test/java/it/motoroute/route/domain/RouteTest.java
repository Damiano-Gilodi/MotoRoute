package it.motoroute.route.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


public class RouteTest {

    @Test
    void createValidRoute() {

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
        assertThat(route.getDifficulty()).isEqualTo(Difficulty.HARD);
        assertThat(route.getDistanceKm()).isEqualTo(new BigDecimal("47.50"));
        assertThat(route.getCreatedAt()).isNotNull();
        assertThat(route.getUpdatedAt()).isEqualTo(route.getCreatedAt());
    }
}
