package it.motoroute.waypoint.application;

import it.motoroute.route.application.RouteNotFoundException;
import it.motoroute.route.domain.Difficulty;
import it.motoroute.route.domain.Route;
import it.motoroute.route.infrastructure.RouteRepository;
import it.motoroute.waypoint.api.CreateWaypointRequest;
import it.motoroute.waypoint.api.WaypointPageResponse;
import it.motoroute.waypoint.api.WaypointResponse;
import it.motoroute.waypoint.api.WaypointSummaryResponse;
import it.motoroute.waypoint.domain.Waypoint;
import it.motoroute.waypoint.infrastructure.WaypointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
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

    @Test
    void shouldFindAllWaypointsByRouteId() {

        Waypoint waypoint1 = Waypoint.create(
            route,
            "name1",
            "waypoint description",
            1,
            new BigDecimal("45.234534"),
            new BigDecimal("125.34455")
        );

        Waypoint waypoint2 = Waypoint.create(
            route,
            "name2",
            null,
            2,
            new BigDecimal("45.234534"),
            new BigDecimal("125.34455")
        );

        Pageable pageable = PageRequest.of(0, 10, Sort.by("position"));

        Page<Waypoint> waypointPage = new PageImpl<>(List.of(waypoint1, waypoint2), pageable, 2);

        when(mockRouteRepository.existsById(routeId)).thenReturn(true);
        when(mockWaypointRepository.findAllByRoute_Id(routeId, pageable)).thenReturn(waypointPage);

        WaypointPageResponse response = waypointService.listWaypoints(routeId, 0, 10);

        assertThat(response.content()).hasSize(2);
        assertThat(response.content()).extracting(WaypointSummaryResponse::position).containsExactly(1, 2);
        assertThat(response.content()).extracting(WaypointSummaryResponse::name).containsExactly("name1", "name2");

        assertThat(response.page()).isEqualTo(0);
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.totalElements()).isEqualTo(2);
        assertThat(response.totalPages()).isEqualTo(1);
        assertThat(response.first()).isTrue();
        assertThat(response.last()).isTrue();

        verify(mockWaypointRepository).findAllByRoute_Id(routeId, pageable);
        verifyNoMoreInteractions(mockWaypointRepository);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -2})
    void shouldRejectNegativePage(int page) {
        when(mockRouteRepository.existsById(routeId)).thenReturn(true);
        assertThatThrownBy(() -> waypointService.listWaypoints(routeId, page, 10))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("page must be greater than or equal to zero");

        verifyNoInteractions(mockWaypointRepository);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 101, 150})
    void shouldRejectInvalidPageSize(int size) {
        when(mockRouteRepository.existsById(routeId)).thenReturn(true);
        assertThatThrownBy(() -> waypointService.listWaypoints(routeId, 0, size))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("size must be between 1 and 100");

        verifyNoInteractions(mockWaypointRepository);
    }

    @Test
    void shouldRejectListingWaypointsForNonExistingRoute() {
        when(mockRouteRepository.existsById(routeId)).thenReturn(false);

        assertThatThrownBy(() -> waypointService.listWaypoints(routeId, 0, 10))
            .isInstanceOf(RouteNotFoundException.class);

        verify(mockRouteRepository).existsById(routeId);
        verifyNoInteractions(mockWaypointRepository);
    }

    @Test
    void shouldReturnRequestedPage() {
        Pageable pageable = PageRequest.of(1, 1, Sort.by("position"));

        Page<Waypoint> waypointPage = new PageImpl<>(
            List.of(Waypoint.create(
                route,
                "name2",
                null,
                2,
                new BigDecimal("45.234534"),
                new BigDecimal("125.34455")
            )),
            pageable,
            2
        );

        when(mockRouteRepository.existsById(routeId)).thenReturn(true);
        when(mockWaypointRepository.findAllByRoute_Id(routeId, pageable)).thenReturn(waypointPage);

        WaypointPageResponse response = waypointService.listWaypoints(routeId, 1, 1);

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().getFirst().position()).isEqualTo(2);

        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(1);
        assertThat(response.totalElements()).isEqualTo(2);
        assertThat(response.totalPages()).isEqualTo(2);
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isTrue();

        verify(mockWaypointRepository).findAllByRoute_Id(routeId, pageable);
    }
}
