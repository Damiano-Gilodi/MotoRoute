package it.motoroute.route.application;

import it.motoroute.route.api.CreateRouteRequest;
import it.motoroute.route.api.RoutePageResponse;
import it.motoroute.route.api.RouteResponse;
import it.motoroute.route.api.RouteSummaryResponse;
import it.motoroute.route.domain.Route;
import org.springframework.data.domain.Page;
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

    public RouteSummaryResponse toSummaryResponse(Route route) {
        return new RouteSummaryResponse(
            route.getId(),
            route.getName(),
            route.getStartLocation(),
            route.getEndLocation(),
            route.getDistanceKm(),
            route.getDifficulty(),
            route.getCreatedAt()
        );
    }

    public RoutePageResponse toPageResponse(Page<Route> routePage) {
        return new RoutePageResponse(
            routePage.getContent()
                .stream()
                .map(this::toSummaryResponse)
                .toList(),
            routePage.getNumber(),
            routePage.getSize(),
            routePage.getTotalElements(),
            routePage.getTotalPages(),
            routePage.isFirst(),
            routePage.isLast()
        );
    }
}
