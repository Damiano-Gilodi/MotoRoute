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
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        Route route = new Route();
        route.id = UUID.randomUUID();
        route.name = name;
        route.description = description;
        route.startLocation = startLocation;
        route.endLocation = endLocation;
        route.distanceKm = distanceKm;
        route.difficulty = difficulty;
        route.createdAt = now;
        route.updatedAt = now;

        return route;
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


