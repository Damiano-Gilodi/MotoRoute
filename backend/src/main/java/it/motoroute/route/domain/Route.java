package it.motoroute.route.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "routes")
public class Route {

    protected Route() {}

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(name="start_location", nullable = false, length = 120)
    private String startLocation;

    @Column(name="end_location", nullable = false, length = 120)
    private String endLocation;

    @Column(name = "distance_km", nullable = false, precision = 8, scale = 2)
    private BigDecimal distanceKm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public static Route create(
        String name,
        String description,
        String startLocation,
        String endLocation,
        BigDecimal distanceKm,
        Difficulty difficulty
    ){

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }

        if (startLocation == null || startLocation.isBlank()) {
            throw new IllegalArgumentException(
                "startLocation must not be blank"
            );
        }

        if (endLocation == null || endLocation.isBlank()) {
            throw new IllegalArgumentException(
                "endLocation must not be blank"
            );
        }

        if (distanceKm == null || distanceKm.signum() <= 0) {
            throw new IllegalArgumentException(
                "distanceKm must be greater than zero"
            );
        }

        if (difficulty == null) {
            throw new IllegalArgumentException(
                "difficulty must not be null"
            );
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        Route route = new Route();
        route.id = UUID.randomUUID();
        route.name = name.trim();
        route.description = normalizeOptionalText(description);
        route.startLocation = startLocation.trim();
        route.endLocation = endLocation.trim();
        route.distanceKm = distanceKm;
        route.difficulty = difficulty;
        route.createdAt = now;
        route.updatedAt = now;

        return route;
    }

    private static String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}


