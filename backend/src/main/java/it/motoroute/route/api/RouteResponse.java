package it.motoroute.route.api;

import io.swagger.v3.oas.annotations.media.Schema;
import it.motoroute.route.domain.Difficulty;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(
    description = "Complete motorcycle route data"
)
public record RouteResponse(

    @Schema(
        description = "Route identifier",
        example = "11111111-1111-1111-1111-111111111111"
    )
    UUID id,

    @Schema(example = "Passo dello Stelvio")
    String name,

    @Schema(
        example = "Percorso panoramico",
        nullable = true
    )
    String description,

    @Schema(example = "Bormio")
    String startLocation,

    @Schema(example = "Prato allo Stelvio")
    String endLocation,

    @Schema(example = "47.50")
    BigDecimal distanceKm,

    @Schema(
        example = "HARD",
        allowableValues = {"EASY", "MEDIUM", "HARD"}
    )
    Difficulty difficulty,

    @Schema(
        description = "Resource creation timestamp",
        example = "2026-07-27T10:00:00Z"
    )
    OffsetDateTime createdAt,

    @Schema(
        description = "Resource last-update timestamp",
        example = "2026-07-27T10:00:00Z"
    )
    OffsetDateTime updatedAt

) {
}
