package it.motoroute.route.api;

import it.motoroute.route.application.RouteService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public ResponseEntity<RouteResponse> createRoute(
        @Valid @RequestBody CreateRouteRequest request
    ) {
        RouteResponse response = routeService.createRoute(request);

        URI location = URI.create("/api/routes/" + response.id());

        return ResponseEntity
            .created(location)
            .body(response);
    }
}
