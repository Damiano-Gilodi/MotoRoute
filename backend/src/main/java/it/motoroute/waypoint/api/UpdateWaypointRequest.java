package it.motoroute.waypoint.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(
    description = "Data required to update a waypoint"
)
public record UpdateWaypointRequest(
    @NotBlank
    @Size(max = 120)
    String name,

    @Size(max = 2000)
    String description,

    @NotNull
    @Positive
    Integer position,

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    @Digits(integer = 2, fraction = 6)
    BigDecimal latitude,

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    @Digits(integer = 3, fraction = 6)
    BigDecimal longitude
) {
}
