package it.motoroute.waypoint.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(
    description = "Complete waypoint data"
)
public record WaypointResponse(

    @Schema(
        description = "Waypoint identifier",
        example = "11111111-1111-1111-1111-111111111111"
    )
    UUID id,

    @Schema(
        description = "Route identifier",
        example = "123e4567-e89b-12d3-a456-426614174000"
    )
    UUID routeId,

    @Schema(
        description = "Waypoint name",
        example = "Punto 1"
    )
    String name,

    @Schema(
        description = "Waypoint description",
        example = "Descrizione opzionale",
        nullable = true
    )
    String description,

    @Schema(
        description = "Waypoint position in the route",
        example = "1"
    )
    Integer position,

    @Schema(
        description = "Waypoint latitude",
        example = "45.555555"
    )
    BigDecimal latitude,

    @Schema(
        description = "Waypoint longitude",
        example = "8.888888"
    )
    BigDecimal longitude,

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
