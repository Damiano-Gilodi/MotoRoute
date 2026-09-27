package it.motoroute.waypoint.application;

import it.motoroute.route.application.RouteNotFoundException;
import it.motoroute.route.domain.Route;
import it.motoroute.route.infrastructure.RouteRepository;
import it.motoroute.waypoint.api.CreateWaypointRequest;
import it.motoroute.waypoint.api.WaypointResponse;
import it.motoroute.waypoint.domain.Waypoint;
import it.motoroute.waypoint.infrastructure.WaypointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class WaypointService {

    private final WaypointMapper waypointMapper;
    private final WaypointRepository waypointRepository;
    private final RouteRepository routeRepository;

    public WaypointService(WaypointMapper waypointMapper, WaypointRepository waypointRepository, RouteRepository routeRepository) {
        this.waypointMapper = waypointMapper;
        this.waypointRepository = waypointRepository;
        this.routeRepository = routeRepository;
    }

    public WaypointResponse createWaypoint(UUID routeId, CreateWaypointRequest request) {

        Route route = routeRepository.findById(routeId)
            .orElseThrow(() -> new RouteNotFoundException(routeId));

        if (waypointRepository.existsByRoute_IdAndPosition(routeId, request.position())) {
            throw new WaypointPositionConflictException(routeId, request.position());
        }

        Waypoint waypoint = waypointMapper.toEntity(route, request);

        Waypoint savedWaypoint = waypointRepository.save(waypoint);

        return waypointMapper.toResponse(savedWaypoint);
    }
}
