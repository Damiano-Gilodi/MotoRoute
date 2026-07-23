package it.motoroute.route.application;

import it.motoroute.route.api.CreateRouteRequest;
import it.motoroute.route.api.RouteResponse;
import it.motoroute.route.domain.Route;
import it.motoroute.route.infrastructure.RouteRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RouteService {

    private final RouteMapper routeMapper;
    private final RouteRepository routeRepository;

    public RouteService(RouteMapper routeMapper, RouteRepository routeRepository) {
        this.routeMapper = routeMapper;
        this.routeRepository = routeRepository;
    }

    public RouteResponse createRoute(CreateRouteRequest request) {
        Route route = routeMapper.toEntity(request);
        Route savedRoute = routeRepository.save(route);
        return routeMapper.toResponse(savedRoute);
    }
}
