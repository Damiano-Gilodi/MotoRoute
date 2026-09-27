package it.motoroute.waypoint.application;

import it.motoroute.route.application.RouteNotFoundException;
import it.motoroute.route.domain.Difficulty;
import it.motoroute.route.domain.Route;
import it.motoroute.route.infrastructure.RouteRepository;
import it.motoroute.waypoint.api.CreateWaypointRequest;
import it.motoroute.waypoint.api.WaypointResponse;
import it.motoroute.waypoint.domain.Waypoint;
import it.motoroute.waypoint.infrastructure.WaypointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WaypointServiceTest {

    private final RouteRepository mockRouteRepository = mock(RouteRepository.class);

    private final WaypointRepository mockWaypointRepository = mock(WaypointRepository.class);

    private WaypointService waypointService;

    private Route route;
    private UUID routeId;

    @BeforeEach
    void setUp() {
        route = Route.create(
            "Passo dello Stelvio",
            "Percorso panoramico",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD
        );

        routeId = route.getId();

        waypointService = new WaypointService(new WaypointMapper(), mockWaypointRepository, mockRouteRepository);
    }

    @Test
    void shouldCreateWaypoint() {

        CreateWaypointRequest request = new CreateWaypointRequest(
            "waypoint name",
            null,
            1,
            new BigDecimal("23.23423"),
            new BigDecimal("100.23423")
        );

        when(mockRouteRepository.findById(routeId)).thenReturn(Optional.of(route));
        when(mockWaypointRepository.existsByRoute_IdAndPosition(routeId, request.position())).thenReturn(false);

        when(mockWaypointRepository.save(any(Waypoint.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        WaypointResponse response = waypointService.createWaypoint(routeId, request);

        assertThat(response.id()).isNotNull();
        assertThat(response.routeId()).isEqualTo(routeId);
        assertThat(response.name()).isEqualTo("waypoint name");
        assertThat(response.description()).isNull();
        assertThat(response.position()).isEqualTo(1);
        assertThat(response.latitude()).isEqualByComparingTo("23.23423");
        assertThat(response.longitude()).isEqualByComparingTo("100.23423");
        assertThat(response.createdAt()).isNotNull();
        assertThat(response.updatedAt()).isEqualTo(response.createdAt());

        verify(mockWaypointRepository).existsByRoute_IdAndPosition(routeId, request.position());
        verify(mockWaypointRepository).save(any(Waypoint.class));
        verifyNoMoreInteractions(mockWaypointRepository);
    }

    @Test
    void shouldThrowRouteNotFoundException() {

        CreateWaypointRequest request = new CreateWaypointRequest(
            "waypoint name",
            null,
            1,
            new BigDecimal("23.23423"),
            new BigDecimal("100.23423")
        );

        when(mockRouteRepository.findById(routeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> waypointService.createWaypoint(routeId, request))
            .isInstanceOf(RouteNotFoundException.class)
            .hasMessage("Route not found with id: " + routeId);

        verify(mockRouteRepository).findById(routeId);
        verifyNoInteractions(mockWaypointRepository);
    }

    @Test
    void shouldThrowWaypointPositionConflictException() {

        CreateWaypointRequest request = new CreateWaypointRequest(
            "waypoint name",
            null,
            1,
            new BigDecimal("23.23423"),
            new BigDecimal("100.23423")
        );

        when(mockRouteRepository.findById(routeId)).thenReturn(Optional.of(route));
        when(mockWaypointRepository.existsByRoute_IdAndPosition(routeId, 1)).thenReturn(true);

        assertThatThrownBy(() -> waypointService.createWaypoint(routeId, request))
            .isInstanceOf(WaypointPositionConflictException.class)
            .hasMessage("Waypoint position " + request.position() + " already exists in route: " + routeId);

        verify(mockWaypointRepository).existsByRoute_IdAndPosition(routeId, 1);
        verify(mockWaypointRepository, never()).save(any(Waypoint.class));
    }
}
