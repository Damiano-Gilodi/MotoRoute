package it.motoroute.route.application;

import it.motoroute.route.api.CreateRouteRequest;
import it.motoroute.route.api.RoutePageResponse;
import it.motoroute.route.api.RouteResponse;
import it.motoroute.route.domain.Route;
import it.motoroute.route.infrastructure.RouteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class RouteService {

    private static final int MAX_PAGE_SIZE = 100;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
        "createdAt",
        "name",
        "startLocation",
        "endLocation",
        "distanceKm",
        "difficulty"
    );

    private final RouteMapper routeMapper;
    private final RouteRepository routeRepository;

    public RouteService(
        RouteMapper routeMapper,
        RouteRepository routeRepository
    ) {
        this.routeMapper = routeMapper;
        this.routeRepository = routeRepository;
    }

    public RouteResponse createRoute(CreateRouteRequest request) {
        Route route = routeMapper.toEntity(request);
        Route savedRoute = routeRepository.save(route);

        return routeMapper.toResponse(savedRoute);
    }

    @Transactional(readOnly = true)
    public RoutePageResponse listRoutes(
        int page,
        int size,
        String sort
    ) {
        validatePagination(page, size);

        Sort.Order sortOrder = parseSort(sort);

        Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by(sortOrder)
        );

        Page<Route> routePage = routeRepository.findAll(pageable);

        return routeMapper.toPageResponse(routePage);
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException(
                "page must be greater than or equal to zero"
            );
        }

        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                "size must be between 1 and " + MAX_PAGE_SIZE
            );
        }
    }

    private Sort.Order parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            throw new IllegalArgumentException(
                "sort must use the format field,direction"
            );
        }

        String[] parts = sort.split(",", -1);

        if (parts.length != 2) {
            throw new IllegalArgumentException(
                "sort must use the format field,direction"
            );
        }

        String field = parts[0].trim();
        String directionValue = parts[1].trim();

        if (field.isEmpty() || directionValue.isEmpty()) {
            throw new IllegalArgumentException(
                "sort must use the format field,direction"
            );
        }

        if (!ALLOWED_SORT_FIELDS.contains(field)) {
            throw new IllegalArgumentException(
                "unsupported sort field: " + field
            );
        }

        Sort.Direction direction;

        try {
            direction = Sort.Direction.fromString(directionValue);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                "sort direction must be asc or desc",
                exception
            );
        }

        return new Sort.Order(direction, field);
    }
}
