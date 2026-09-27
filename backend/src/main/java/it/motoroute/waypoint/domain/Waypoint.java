package it.motoroute.waypoint.domain;

import it.motoroute.route.domain.Route;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "waypoints")
public class Waypoint {

    private static final int MAX_NAME_LENGTH = 120;
    private static final int MAX_DESCRIPTION_LENGTH = 2000;
    private static final BigDecimal MAX_LATITUDE = BigDecimal.valueOf(90);
    private static final BigDecimal MIN_LATITUDE = BigDecimal.valueOf(-90);
    private static final BigDecimal MAX_LONGITUDE = BigDecimal.valueOf(180);
    private static final BigDecimal MIN_LONGITUDE = BigDecimal.valueOf(-180);

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Integer position;

    @Column(nullable = false, precision = 8, scale = 6)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected Waypoint() {
    }

    public static Waypoint create(
        Route route,
        String name,
        String description,
        Integer position,
        BigDecimal latitude,
        BigDecimal longitude
    ) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        Waypoint waypoint = new Waypoint();
        waypoint.id = UUID.randomUUID();
        waypoint.route = validateRoute(route);
        waypoint.name = validateName(name);
        waypoint.description = validateDescription(description);
        waypoint.position = validatePosition(position);
        waypoint.latitude = validateLatitude(latitude);
        waypoint.longitude = validateLongitude(longitude);
        waypoint.createdAt = now;
        waypoint.updatedAt = now;

        return waypoint;
    }

    private static Route validateRoute(Route route) {
        if (route == null) {
            throw new IllegalArgumentException("route must not be null");
        }
        return route;
    }

    private static String validateName(String name) {
        return normalizeText(name, "name", MAX_NAME_LENGTH, true);
    }

    private static String validateDescription(String description) {
        return normalizeText(description, "description", MAX_DESCRIPTION_LENGTH, false);
    }

    private static Integer validatePosition(Integer position) {
        if (position == null || position <= 0) {
            throw new IllegalArgumentException("position must be greater than zero");
        }
        return position;
    }

    private static BigDecimal validateLatitude(BigDecimal latitude) {
        return validateCoordinate(latitude, "latitude", MIN_LATITUDE, MAX_LATITUDE);
    }

    private static BigDecimal validateLongitude(BigDecimal longitude) {
        return validateCoordinate(longitude, "longitude", MIN_LONGITUDE, MAX_LONGITUDE);
    }

    private static String normalizeText(
        String value,
        String fieldName,
        int maxLength,
        boolean required
    ) {
        if (value == null || value.isBlank()) {
            if (required) {
                throw new IllegalArgumentException(fieldName + " must not be blank");
            } else {
                return null;
            }
        }

        String normalizedValue = value.trim();

        if (normalizedValue.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " must not exceed " + maxLength + " characters");
        }

        return normalizedValue;
    }

    private static BigDecimal validateCoordinate(
        BigDecimal value,
        String fieldName,
        BigDecimal min,
        BigDecimal max
    ) {
        if (value == null || value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            throw new IllegalArgumentException(fieldName + " must be between " + min + " and " + max);
        }

        if (value.scale() > 6) {
            throw new IllegalArgumentException(fieldName + " must not exceed 6 decimal places");
        }

        return value;
    }

    public UUID getId() {
        return id;
    }

    public UUID getRouteId() {
        return route.getId();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPosition() {
        return position;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
