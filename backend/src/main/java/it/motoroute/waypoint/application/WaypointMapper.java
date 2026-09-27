package it.motoroute.waypoint.application;

import it.motoroute.route.domain.Route;
import it.motoroute.waypoint.api.CreateWaypointRequest;
import it.motoroute.waypoint.api.WaypointResponse;
import it.motoroute.waypoint.domain.Waypoint;
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
}
