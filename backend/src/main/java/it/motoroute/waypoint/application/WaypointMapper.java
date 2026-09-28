package it.motoroute.waypoint.application;

import it.motoroute.route.domain.Route;
import it.motoroute.waypoint.api.CreateWaypointRequest;
import it.motoroute.waypoint.api.WaypointPageResponse;
import it.motoroute.waypoint.api.WaypointResponse;
import it.motoroute.waypoint.api.WaypointSummaryResponse;
import it.motoroute.waypoint.domain.Waypoint;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class WaypointMapper {
    public Waypoint toEntity(Route route, CreateWaypointRequest request) {

        return Waypoint.create(
            route,
            request.name(),
            request.description(),
            request.position(),
            request.latitude(),
            request.longitude()
        );
    }

    public WaypointResponse toResponse(Waypoint waypoint) {

        return new WaypointResponse(
            waypoint.getId(),
            waypoint.getRouteId(),
            waypoint.getName(),
            waypoint.getDescription(),
            waypoint.getPosition(),
            waypoint.getLatitude(),
            waypoint.getLongitude(),
            waypoint.getCreatedAt(),
            waypoint.getUpdatedAt()
        );
    }

    public WaypointSummaryResponse toSummaryResponse(Waypoint waypoint) {

        return new WaypointSummaryResponse(
            waypoint.getId(),
            waypoint.getName(),
            waypoint.getPosition(),
            waypoint.getLatitude(),
            waypoint.getLongitude(),
            waypoint.getCreatedAt()
        );
    }

    public WaypointPageResponse toPageResponse(Page<Waypoint> waypointPage) {

        return new WaypointPageResponse(
            waypointPage.getContent()
                .stream()
                .map(this::toSummaryResponse)
                .toList(),
            waypointPage.getNumber(),
            waypointPage.getSize(),
            waypointPage.getTotalElements(),
            waypointPage.getTotalPages(),
            waypointPage.isFirst(),
            waypointPage.isLast()
        );
    }
}
