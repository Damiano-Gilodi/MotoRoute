package it.motoroute.waypoint.application;

import it.motoroute.route.application.RouteNotFoundException;
import it.motoroute.route.domain.Difficulty;
import it.motoroute.route.domain.Route;
import it.motoroute.route.infrastructure.RouteRepository;
import it.motoroute.waypoint.api.*;
import it.motoroute.waypoint.domain.Waypoint;
import it.motoroute.waypoint.infrastructure.WaypointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
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

    @Test
    void shouldUpdateWaypoint() {

        Waypoint waypoint = Waypoint.create(
            route,
            "name",
            "waypoint description",
            1,
            new BigDecimal("45.234534"),
            new BigDecimal("125.34455")
        );

        UUID waypointId = waypoint.getId();
        UUID routeId = waypoint.getRouteId();
        OffsetDateTime createdAt = waypoint.getCreatedAt();
        OffsetDateTime updatedAt = waypoint.getUpdatedAt();

        when(mockRouteRepository.existsById(routeId)).thenReturn(true);
        when(mockWaypointRepository.findByIdAndRoute_Id(waypointId, routeId)).thenReturn(Optional.of(waypoint));

        UpdateWaypointRequest request = new UpdateWaypointRequest(
            "name modifica",
            "waypoint description modifica",
            2,
            new BigDecimal("24.4534"),
            new BigDecimal("15.34455")
        );

        when(mockWaypointRepository.existsByRoute_IdAndPosition(routeId, request.position())).thenReturn(false);

        WaypointResponse updatedWaypoint = waypointService.updateWaypoint(routeId, waypointId, request);

        assertThat(updatedWaypoint.id()).isEqualTo(waypointId);
        assertThat(updatedWaypoint.routeId()).isEqualTo(routeId);
        assertThat(updatedWaypoint.name()).isEqualTo(request.name());
        assertThat(updatedWaypoint.description()).isEqualTo(request.description());
        assertThat(updatedWaypoint.position()).isEqualTo(request.position());
        assertThat(updatedWaypoint.latitude()).isEqualTo(request.latitude());
        assertThat(updatedWaypoint.longitude()).isEqualTo(request.longitude());
        assertThat(updatedWaypoint.createdAt()).isEqualTo(createdAt);
        assertThat(updatedWaypoint.updatedAt()).isAfterOrEqualTo(updatedAt);

        verify(mockRouteRepository).existsById(routeId);
        verify(mockWaypointRepository).findByIdAndRoute_Id(waypointId, routeId);
        verify(mockWaypointRepository).existsByRoute_IdAndPosition(routeId, request.position());
    }

    @Test
    void shouldRejectUpdateWhenRouteDoesNotExist() {
        UUID waypointId = UUID.randomUUID();

        UpdateWaypointRequest request = new UpdateWaypointRequest(
            "name modifica",
            "waypoint description modifica",
            2,
            new BigDecimal("24.4534"),
            new BigDecimal("15.34455")
        );

        when(mockRouteRepository.existsById(routeId)).thenReturn(false);

        assertThatThrownBy(() -> waypointService.updateWaypoint(routeId, waypointId, request))
            .isInstanceOf(RouteNotFoundException.class)
            .hasMessage("Route not found with id: " + routeId);

        verify(mockRouteRepository).existsById(routeId);
        verifyNoInteractions(mockWaypointRepository);
    }

    @Test
    void shouldRejectUpdateWhenWaypointDoesNotExistInRoute() {
        UUID waypointId = UUID.randomUUID();

        UpdateWaypointRequest request = new UpdateWaypointRequest(
            "name modifica",
            "waypoint description modifica",
            2,
            new BigDecimal("24.4534"),
            new BigDecimal("15.34455")
        );

        when(mockRouteRepository.existsById(routeId)).thenReturn(true);
        when(mockWaypointRepository.findByIdAndRoute_Id(waypointId, routeId))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> waypointService.updateWaypoint(routeId, waypointId, request))
            .isInstanceOf(WaypointNotFoundException.class)
            .hasMessage("Waypoint not found with id: " + waypointId);

        verify(mockRouteRepository).existsById(routeId);
        verify(mockWaypointRepository).findByIdAndRoute_Id(waypointId, routeId);
        verify(mockWaypointRepository, never())
            .existsByRoute_IdAndPosition(any(UUID.class), anyInt());
    }

    @Test
    void shouldRejectUpdateWhenNewPositionAlreadyExists() {
        Waypoint waypoint = Waypoint.create(
            route,
            "name",
            "waypoint description",
            1,
            new BigDecimal("45.234534"),
            new BigDecimal("125.34455")
        );

        UUID waypointId = waypoint.getId();

        UpdateWaypointRequest request = new UpdateWaypointRequest(
            "name modifica",
            "waypoint description modifica",
            2,
            new BigDecimal("24.4534"),
            new BigDecimal("15.34455")
        );

        when(mockRouteRepository.existsById(routeId)).thenReturn(true);
        when(mockWaypointRepository.findByIdAndRoute_Id(waypointId, routeId))
            .thenReturn(Optional.of(waypoint));
        when(mockWaypointRepository.existsByRoute_IdAndPosition(routeId, 2))
            .thenReturn(true);

        assertThatThrownBy(() -> waypointService.updateWaypoint(routeId, waypointId, request))
            .isInstanceOf(WaypointPositionConflictException.class)
            .hasMessage("Waypoint position 2 already exists in route: " + routeId);

        assertThat(waypoint.getName()).isEqualTo("name");
        assertThat(waypoint.getPosition()).isEqualTo(1);

        verify(mockWaypointRepository).findByIdAndRoute_Id(waypointId, routeId);
        verify(mockWaypointRepository).existsByRoute_IdAndPosition(routeId, 2);
    }

    @Test
    void shouldAllowUpdateWhenPositionDoesNotChange() {
        Waypoint waypoint = Waypoint.create(
            route,
            "name",
            "waypoint description",
            1,
            new BigDecimal("45.234534"),
            new BigDecimal("125.34455")
        );

        UUID waypointId = waypoint.getId();

        UpdateWaypointRequest request = new UpdateWaypointRequest(
            "name modifica",
            "description modifica",
            1,
            new BigDecimal("24.4534"),
            new BigDecimal("15.34455")
        );

        when(mockRouteRepository.existsById(routeId)).thenReturn(true);
        when(mockWaypointRepository.findByIdAndRoute_Id(waypointId, routeId))
            .thenReturn(Optional.of(waypoint));

        WaypointResponse response = waypointService.updateWaypoint(routeId, waypointId, request);

        assertThat(response.name()).isEqualTo("name modifica");
        assertThat(response.description()).isEqualTo("description modifica");
        assertThat(response.position()).isEqualTo(1);

        verify(mockWaypointRepository).findByIdAndRoute_Id(waypointId, routeId);
        verify(mockWaypointRepository, never())
            .existsByRoute_IdAndPosition(any(UUID.class), anyInt());
    }

    @Test
    void shouldGetWaypoint() {

        Waypoint waypoint = Waypoint.create(
            route,
            "Punto panoramico",
            "Belvedere con vista sulle montagne",
            1,
            new BigDecimal("45.123456"),
            new BigDecimal("8.765432")
        );

        UUID waypointId = waypoint.getId();

        when(mockRouteRepository.existsById(routeId)).thenReturn(true);
        when(mockWaypointRepository.findByIdAndRoute_Id(waypointId, routeId))
            .thenReturn(Optional.of(waypoint));

        WaypointResponse response = waypointService.getWaypoint(routeId, waypointId);

        assertThat(response.id()).isEqualTo(waypointId);
        assertThat(response.routeId()).isEqualTo(routeId);
        assertThat(response.name()).isEqualTo("Punto panoramico");
        assertThat(response.description()).isEqualTo("Belvedere con vista sulle montagne");
        assertThat(response.position()).isEqualTo(1);
        assertThat(response.latitude()).isEqualByComparingTo("45.123456");
        assertThat(response.longitude()).isEqualByComparingTo("8.765432");

        verify(mockRouteRepository).existsById(routeId);
        verify(mockWaypointRepository).findByIdAndRoute_Id(waypointId, routeId);
    }

    @Test
    void shouldRejectGettingWaypointWhenRouteDoesNotExist() {

        UUID waypointId = UUID.randomUUID();

        when(mockRouteRepository.existsById(routeId)).thenReturn(false);

        assertThatThrownBy(() -> waypointService.getWaypoint(routeId, waypointId))
            .isInstanceOf(RouteNotFoundException.class)
            .hasMessage("Route not found with id: " + routeId);

        verify(mockRouteRepository).existsById(routeId);
        verifyNoInteractions(mockWaypointRepository);
    }

    @Test
    void shouldRejectGettingWaypointWhenWaypointDoesNotExistInRoute() {

        UUID waypointId = UUID.randomUUID();

        when(mockRouteRepository.existsById(routeId)).thenReturn(true);
        when(mockWaypointRepository.findByIdAndRoute_Id(waypointId, routeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> waypointService.getWaypoint(routeId, waypointId))
            .isInstanceOf(WaypointNotFoundException.class)
            .hasMessage("Waypoint not found with id: " + waypointId);

        verify(mockRouteRepository).existsById(routeId);
        verify(mockWaypointRepository).findByIdAndRoute_Id(waypointId, routeId);
    }

    @Test
    void shouldDeleteWaypoint() {

        Waypoint waypoint = Waypoint.create(
            route,
            "Punto panoramico",
            "Belvedere con vista sulle montagne",
            1,
            new BigDecimal("45.123456"),
            new BigDecimal("8.765432")
        );

        UUID waypointId = waypoint.getId();

        when(mockRouteRepository.existsById(routeId)).thenReturn(true);
        when(mockWaypointRepository.findByIdAndRoute_Id(waypointId, routeId))
            .thenReturn(Optional.of(waypoint));

        waypointService.deleteWaypoint(routeId, waypointId);

        verify(mockRouteRepository).existsById(routeId);
        verify(mockWaypointRepository).findByIdAndRoute_Id(waypointId, routeId);
        verify(mockWaypointRepository).delete(waypoint);
        verifyNoMoreInteractions(mockRouteRepository);
    }

    @Test
    void shouldRejectDeleteWaypointWhenRouteDoesNotExist() {

        UUID waypointId = UUID.randomUUID();

        when(mockRouteRepository.existsById(routeId)).thenReturn(false);

        assertThatThrownBy(() -> waypointService.deleteWaypoint(routeId, waypointId))
            .isInstanceOf(RouteNotFoundException.class)
            .hasMessage("Route not found with id: " + routeId);

        verify(mockRouteRepository).existsById(routeId);
        verifyNoInteractions(mockWaypointRepository);
    }

    @Test
    void shouldRejectDeleteWaypointWhenWaypointDoesNotExistInRoute() {

        UUID waypointId = UUID.randomUUID();

        when(mockRouteRepository.existsById(routeId)).thenReturn(true);
        when(mockWaypointRepository.findByIdAndRoute_Id(waypointId, routeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> waypointService.deleteWaypoint(routeId, waypointId))
            .isInstanceOf(WaypointNotFoundException.class)
            .hasMessage("Waypoint not found with id: " + waypointId);

        verify(mockRouteRepository).existsById(routeId);
        verify(mockWaypointRepository).findByIdAndRoute_Id(waypointId, routeId);
    }
}
