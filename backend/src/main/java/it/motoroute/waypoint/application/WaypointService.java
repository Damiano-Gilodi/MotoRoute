package it.motoroute.waypoint.application;

import it.motoroute.route.application.RouteNotFoundException;
import it.motoroute.route.domain.Route;
import it.motoroute.route.infrastructure.RouteRepository;
import it.motoroute.waypoint.api.CreateWaypointRequest;
import it.motoroute.waypoint.api.WaypointPageResponse;
import it.motoroute.waypoint.api.WaypointResponse;
import it.motoroute.waypoint.domain.Waypoint;
import it.motoroute.waypoint.infrastructure.WaypointRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class WaypointService {

    private static final int MAX_PAGE_SIZE = 100;

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

    @Transactional(readOnly = true)
    public WaypointPageResponse listWaypoints(UUID routeId, int page, int size) {

        validatePagination(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("position"));

        if (!routeRepository.existsById(routeId)) {
            throw new RouteNotFoundException(routeId);
        }

        Page<Waypoint> waypointPage = waypointRepository.findAllByRoute_Id(routeId, pageable);

        return waypointMapper.toPageResponse(waypointPage);
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("page must be greater than or equal to zero");
        }

        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("size must be between 1 and " + MAX_PAGE_SIZE);
        }
    }
}
