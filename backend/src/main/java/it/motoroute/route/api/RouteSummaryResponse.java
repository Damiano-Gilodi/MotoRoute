package it.motoroute.route.api;

import io.swagger.v3.oas.annotations.media.Schema;
import it.motoroute.route.domain.Difficulty;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(
    description = "Synthetic route data used in paginated route lists"
)
public record RouteSummaryResponse(

    @Schema(
        description = "Route identifier",
        example = "11111111-1111-1111-1111-111111111111"
    )
    UUID id,

    @Schema(
        description = "Route name",
        example = "Passo dello Stelvio"
    )
    String name,

    @Schema(
        description = "Route start location",
        example = "Bormio"
    )
    String startLocation,

    @Schema(
        description = "Route end location",
        example = "Prato allo Stelvio"
    )
    String endLocation,

    @Schema(
        description = "Route distance in kilometres",
        example = "47.50"
    )
    BigDecimal distanceKm,

    @Schema(
        description = "Route difficulty level",
        example = "HARD",
        allowableValues = {"EASY", "MEDIUM", "HARD"}
    )
    Difficulty difficulty,

    @Schema(
        description = "Resource creation timestamp",
        example = "2026-07-27T10:00:00Z"
    )
    OffsetDateTime createdAt
) {
}
