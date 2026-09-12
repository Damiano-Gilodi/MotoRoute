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

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        Route route = new Route();
        route.id = UUID.randomUUID();
        route.name = validateName(name);
        route.description = validateDescription(description);
        route.startLocation = validateStartLocation(startLocation);
        route.endLocation = validateEndLocation(endLocation);
        route.distanceKm = validateDistanceKm(distanceKm);
        route.difficulty = validateDifficulty(difficulty);
        route.createdAt = now;
        route.updatedAt = now;

        return route;
    }

    public void update(
        String name,
        String description,
        String startLocation,
        String endLocation,
        BigDecimal distanceKm,
        Difficulty difficulty
    ) {
        String validatedName = validateName(name);
        String validatedDescription = validateDescription(description);
        String validatedStartLocation = validateStartLocation(startLocation);
        String validatedEndLocation = validateEndLocation(endLocation);
        BigDecimal validatedDistanceKm = validateDistanceKm(distanceKm);
        Difficulty validatedDifficulty = validateDifficulty(difficulty);

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        this.name = validatedName;
        this.description = validatedDescription;
        this.startLocation = validatedStartLocation;
        this.endLocation = validatedEndLocation;
        this.distanceKm = validatedDistanceKm;
        this.difficulty = validatedDifficulty;
        this.updatedAt = now;
    }

    private static String validateName(String name) {
        return normalizeText(name, "name", MAX_NAME_LENGTH, true);
    }

    private static String validateDescription(String description) {
        return normalizeText(description, "description", MAX_DESCRIPTION_LENGTH, false);
    }

    private static String validateStartLocation(String startLocation) {
        return normalizeText(startLocation, "startLocation", MAX_LOCATION_LENGTH, true);
    }

    private static String validateEndLocation(String endLocation) {
        return normalizeText(endLocation, "endLocation", MAX_LOCATION_LENGTH, true);
    }

    private static BigDecimal validateDistanceKm(BigDecimal distanceKm) {
        if (distanceKm == null || distanceKm.signum() <= 0) {
            throw new IllegalArgumentException("distanceKm must be greater than zero");
        }
        return distanceKm;
    }

    private static Difficulty validateDifficulty(Difficulty difficulty) {
        if (difficulty == null) {
            throw new IllegalArgumentException("difficulty must not be null");
        }
        return difficulty;
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


