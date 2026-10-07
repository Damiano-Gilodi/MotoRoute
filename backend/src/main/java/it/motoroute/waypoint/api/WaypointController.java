package it.motoroute.waypoint.api;

import it.motoroute.waypoint.application.WaypointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
public class WaypointController implements WaypointApi {

    private final WaypointService waypointService;

    public WaypointController(WaypointService waypointService) {
        this.waypointService = waypointService;
    }

    @Override
    public ResponseEntity<WaypointResponse> createWaypoint(
        UUID routeId,
        CreateWaypointRequest request
    ) {
        WaypointResponse response = waypointService.createWaypoint(routeId, request);
        URI location = URI.create("/api/routes/" + routeId + "/waypoints/" + response.id());

        return ResponseEntity.created(location).body(response);
    }

    @Override
    public ResponseEntity<WaypointPageResponse> listWaypoints(
        UUID routeId,
        int page,
        int size
    ) {
        return ResponseEntity.ok(waypointService.listWaypoints(routeId, page, size));
    }

    @Override
    public ResponseEntity<WaypointResponse> getWaypoint(
        UUID routeId,
        UUID waypointId
    ) {
        return ResponseEntity.ok(waypointService.getWaypoint(routeId, waypointId));
    }

    @Override
    public ResponseEntity<WaypointResponse> updateWaypoint(
        UUID routeId,
        UUID waypointId,
        UpdateWaypointRequest request
    ) {
        return ResponseEntity.ok(waypointService.updateWaypoint(routeId, waypointId, request));
    }
}
