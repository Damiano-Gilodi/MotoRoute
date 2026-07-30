package it.motoroute.route.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "routes")
public class Route {

    private static final int MAX_NAME_LENGTH = 120;
    private static final int MAX_DESCRIPTION_LENGTH = 2000;
    private static final int MAX_LOCATION_LENGTH = 120;

    protected Route() {
    }

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(name = "start_location", nullable = false, length = 120)
    private String startLocation;

    @Column(name = "end_location", nullable = false, length = 120)
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
    ) {
        String normalizedName = normalizeText(
            name,
            "name",
            MAX_NAME_LENGTH,
            true
        );

        String normalizedDescription = normalizeText(
            description,
            "description",
            MAX_DESCRIPTION_LENGTH,
            false
        );

        String normalizedStartLocation = normalizeText(
            startLocation,
            "startLocation",
            MAX_LOCATION_LENGTH,
            true
        );

        String normalizedEndLocation = normalizeText(
            endLocation,
            "endLocation",
            MAX_LOCATION_LENGTH,
            true
        );

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
        route.name = normalizedName;
        route.description = normalizedDescription;
        route.startLocation = normalizedStartLocation;
        route.endLocation = normalizedEndLocation;
        route.distanceKm = distanceKm;
        route.difficulty = difficulty;
        route.createdAt = now;
        route.updatedAt = now;

        return route;
    }

    private static String normalizeText(
        String value,
        String fieldName,
        int maxLength,
        boolean required
    ) {
        if (value == null || value.isBlank()) {
            if (required) {
                throw new IllegalArgumentException(
                    fieldName + " must not be blank"
                );
            } else {
                return null;
            }
        }

        String normalizedValue = value.trim();

        if (normalizedValue.length() > maxLength) {
            throw new IllegalArgumentException(
                fieldName
                    + " must not exceed "
                    + maxLength
                    + " characters"
            );
        }

        return normalizedValue;
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


