package it.motoroute.waypoint.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(
    description = "Data required to create a waypoint"
)
public record CreateWaypointRequest(

    @Schema(
        description = "Waypoint name",
        example = "Punto 1",
        maxLength = 120
    )
    @NotBlank
    @Size(max = 120)
    String name,

    @Schema(
        description = "Optional waypoint description",
        example = "Descrizione opzionale",
        maxLength = 2000
    )
    @Size(max = 2000)
    String description,

    @Schema(
        description = "Waypoint position in the route",
        example = "1",
        minimum = "1"
    )
    @NotNull
    @Positive
    Integer position,

    @Schema(
        description = "Waypoint latitude",
        example = "45.555555",
        minimum = "-90.0",
        maximum = "90.0",
        multipleOf = 0.000001
    )
    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    @Digits(integer = 2, fraction = 6)
    BigDecimal latitude,

    @Schema(
        description = "Waypoint longitude",
        example = "8.888888",
        minimum = "-180.0",
        maximum = "180.0",
        multipleOf = 0.000001
    )
    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    @Digits(integer = 3, fraction = 6)
    BigDecimal longitude
) {
}
