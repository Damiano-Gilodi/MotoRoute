package it.motoroute.route.application;

import it.motoroute.route.api.CreateRouteRequest;
import it.motoroute.route.api.RouteResponse;
import it.motoroute.route.domain.Route;
import org.springframework.stereotype.Component;

@Component
public class RouteMapper {

    public Route toEntity(CreateRouteRequest request) {
        return Route.create(
            request.name(),
            request.description(),
            request.startLocation(),
            request.endLocation(),
            request.distanceKm(),
            request.difficulty()
        );
    }

    public RouteResponse toResponse(Route route) {
        return new RouteResponse(
            route.getId(),
            route.getName(),
            route.getDescription(),
            route.getStartLocation(),
            route.getEndLocation(),
            route.getDistanceKm(),
            route.getDifficulty(),
            route.getCreatedAt(),
            route.getUpdatedAt()
        );
    }
}
