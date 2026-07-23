package it.motoroute.route.api;

import it.motoroute.route.domain.Difficulty;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RouteResponse(

    UUID id,
    String name,
    String description,
    String startLocation,
    String endLocation,
    BigDecimal distanceKm,
    Difficulty difficulty,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}
