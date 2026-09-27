package it.motoroute.waypoint.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(
    description = "Represents a waypoint summary"
)
public record WaypointSummaryResponse(

    @Schema(
        description = "Unique identifier of the waypoint",
        examples = "11111111-1111-1111-1111-111111111111"

    )
    UUID id,

    @Schema(
        description = "Name of the waypoint",
        example = "Punto panoramico"
    )
    String name,

    @Schema(
        description = "Description of the waypoint",
        example = "Vista sulle montagne"
    )
    String description,

    @Schema(
        description = "Position of the waypoint",
        example = "1"
    )
    Integer position,

    @Schema(
        description = "Latitude of the waypoint",
        example = "45.123456"
    )
    BigDecimal latitude,

    @Schema(
        description = "Longitude of the waypoint",
        example = "9.123456"
    )
    BigDecimal longitude,

    @Schema(
        description = "Creation date of the waypoint",
        example = "2023-01-01T00:00:00+00:00"
    )
    OffsetDateTime createdAt
) {
}
