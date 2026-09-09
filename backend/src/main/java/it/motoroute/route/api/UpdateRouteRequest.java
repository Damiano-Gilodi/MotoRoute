package it.motoroute.route.api;

import io.swagger.v3.oas.annotations.media.Schema;
import it.motoroute.route.domain.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(
    description = "Data required to update a motorcycle route"
)
public record UpdateRouteRequest(
    @Schema(
        description = "Route name",
        example = "Passo dello Stelvio",
        maxLength = 120
    )
    @NotBlank
    @Size(max = 120)
    String name,

    @Schema(
        description = "Optional route description",
        example = "Percorso panoramico",
        maxLength = 2000,
        nullable = true
    )
    @Size(max = 2000)
    String description,

    @Schema(
        description = "Route starting location",
        example = "Bormio",
        maxLength = 120
    )
    @NotBlank
    @Size(max = 120)
    String startLocation,

    @Schema(
        description = "Route ending location",
        example = "Prato allo Stelvio",
        maxLength = 120
    )
    @NotBlank
    @Size(max = 120)
    String endLocation,

    @Schema(
        description = "Route distance in kilometres",
        example = "47.50",
        minimum = "0",
        exclusiveMinimum = true
    )
    @NotNull
    @Positive
    BigDecimal distanceKm,

    @Schema(
        description = "Route difficulty",
        example = "HARD",
        allowableValues = {"EASY", "MEDIUM", "HARD"}
    )
    @NotNull
    Difficulty difficulty
) {
}
