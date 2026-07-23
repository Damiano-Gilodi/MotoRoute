package it.motoroute.route.api;

import it.motoroute.route.domain.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateRouteRequest(

    @NotBlank
    @Size(max = 120)
    String name,

    @Size(max = 2000)
    String description,

    @NotBlank
    @Size(max = 120)
    String startLocation,

    @NotBlank
    @Size(max = 120)
    String endLocation,

    @NotNull
    @Positive
    BigDecimal distanceKm,

    @NotNull
    Difficulty difficulty
) {
}
