package it.motoroute.route.api;

import it.motoroute.route.application.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
public class RouteController implements RouteApi {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @Override
    public ResponseEntity<RouteResponse> createRoute(CreateRouteRequest request) {
        RouteResponse response = routeService.createRoute(request);
        URI location = URI.create("/api/routes/" + response.id());

        return ResponseEntity.created(location).body(response);
    }

    @Override
    public ResponseEntity<RoutePageResponse> listRoutes(int page, int size, String sort) {
        return ResponseEntity.ok(routeService.listRoutes(page, size, sort));
    }

    @Override
    public ResponseEntity<RouteResponse> getRoute(UUID routeId) {
        return ResponseEntity.ok(routeService.getRoute(routeId));
    }

    @Override
    public ResponseEntity<RouteResponse> updateRoute(UUID routeId, UpdateRouteRequest request) {
        return ResponseEntity.ok(routeService.updateRoute(routeId, request));
    }

    @Override
    public ResponseEntity<Void> deleteRoute(UUID routeId) {
        routeService.deleteRoute(routeId);
        return ResponseEntity.noContent().build();
    }
}
