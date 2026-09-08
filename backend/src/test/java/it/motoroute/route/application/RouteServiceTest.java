package it.motoroute.route.application;

import it.motoroute.route.api.CreateRouteRequest;
import it.motoroute.route.api.RoutePageResponse;
import it.motoroute.route.api.RouteResponse;
import it.motoroute.route.api.UpdateRouteRequest;
import it.motoroute.route.domain.Difficulty;
import it.motoroute.route.domain.Route;
import it.motoroute.route.infrastructure.RouteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RouteServiceTest {

    private RouteRepository mockRouteRepository;
    private RouteService routeService;

    @BeforeEach
    void setUp() {
        RouteMapper routeMapper = new RouteMapper();

        mockRouteRepository = mock(RouteRepository.class);
        routeService = new RouteService(routeMapper, mockRouteRepository);
    }

    @Test
    void shouldCreateRoute() {
        CreateRouteRequest request = new CreateRouteRequest(
            "  Passo dello Stelvio  ",
            "  Percorso panoramico  ",
            "  Bormio  ",
            "  Prato allo Stelvio  ",
            new BigDecimal("47.50"),
            Difficulty.HARD
        );

        when(mockRouteRepository.save(any(Route.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        RouteResponse routeResponse =
            routeService.createRoute(request);

        assertThat(routeResponse.id()).isNotNull();
        assertThat(routeResponse.name())
            .isEqualTo("Passo dello Stelvio");
        assertThat(routeResponse.description())
            .isEqualTo("Percorso panoramico");
        assertThat(routeResponse.startLocation())
            .isEqualTo("Bormio");
        assertThat(routeResponse.endLocation())
            .isEqualTo("Prato allo Stelvio");
        assertThat(routeResponse.distanceKm())
            .isEqualByComparingTo("47.50");
        assertThat(routeResponse.difficulty())
            .isEqualTo(Difficulty.HARD);
        assertThat(routeResponse.createdAt()).isNotNull();
        assertThat(routeResponse.updatedAt()).isNotNull();

        verify(mockRouteRepository)
            .save(any(Route.class));

        verifyNoMoreInteractions(mockRouteRepository);
    }

    @Test
    void shouldReturnPaginatedAndSortedRoutes() {
        Route firstRoute = createRoute(
            "Passo dello Stelvio",
            "Bormio",
            "Prato allo Stelvio",
            "47.50",
            Difficulty.HARD
        );

        Route secondRoute = createRoute(
            "Lago di Garda",
            "Riva del Garda",
            "Sirmione",
            "96.30",
            Difficulty.MEDIUM
        );

        Pageable repositoryPageable = PageRequest.of(
            1,
            5,
            Sort.by(
                Sort.Order.desc("createdAt")
            )
        );

        Page<Route> repositoryPage = new PageImpl<>(
            List.of(firstRoute, secondRoute),
            repositoryPageable,
            12
        );

        when(mockRouteRepository.findAll(any(Pageable.class)))
            .thenReturn(repositoryPage);

        RoutePageResponse response = routeService.listRoutes(
            1,
            5,
            "createdAt,desc"
        );

        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(5);
        assertThat(response.totalElements()).isEqualTo(12);
        assertThat(response.totalPages()).isEqualTo(3);
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isFalse();

        assertThat(response.content()).hasSize(2);

        assertThat(response.content().getFirst().id())
            .isEqualTo(firstRoute.getId());
        assertThat(response.content().getFirst().name())
            .isEqualTo("Passo dello Stelvio");
        assertThat(response.content().getFirst().startLocation())
            .isEqualTo("Bormio");
        assertThat(response.content().getFirst().endLocation())
            .isEqualTo("Prato allo Stelvio");
        assertThat(response.content().getFirst().distanceKm())
            .isEqualByComparingTo("47.50");
        assertThat(response.content().getFirst().difficulty())
            .isEqualTo(Difficulty.HARD);
        assertThat(response.content().getFirst().createdAt())
            .isEqualTo(firstRoute.getCreatedAt());

        ArgumentCaptor<Pageable> pageableCaptor =
            ArgumentCaptor.forClass(Pageable.class);

        verify(mockRouteRepository)
            .findAll(pageableCaptor.capture());

        Pageable requestedPageable = pageableCaptor.getValue();

        assertThat(requestedPageable.getPageNumber())
            .isEqualTo(1);
        assertThat(requestedPageable.getPageSize())
            .isEqualTo(5);

        Sort.Order sortOrder = requestedPageable
            .getSort()
            .getOrderFor("createdAt");

        assertThat(sortOrder).isNotNull();
        assertThat(sortOrder.getDirection())
            .isEqualTo(Sort.Direction.DESC);

        verifyNoMoreInteractions(mockRouteRepository);
    }

    @Test
    void shouldRejectNegativePage() {
        assertThatThrownBy(() ->
            routeService.listRoutes(
                -1,
                20,
                "createdAt,desc"
            )
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(
                "page must be greater than or equal to zero"
            );

        verifyNoInteractions(mockRouteRepository);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 101})
    void shouldRejectInvalidPageSize(int size) {
        assertThatThrownBy(() ->
            routeService.listRoutes(
                0,
                size,
                "createdAt,desc"
            )
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("size must be between 1 and 100");

        verifyNoInteractions(mockRouteRepository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
        " ",
        "createdAt",
        "createdAt,",
        ",desc",
        "createdAt,desc,extra"
    })
    void shouldRejectMalformedSort(String sort) {
        assertThatThrownBy(() ->
            routeService.listRoutes(
                0,
                20,
                sort
            )
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(
                "sort must use the format field,direction"
            );

        verifyNoInteractions(mockRouteRepository);
    }

    @Test
    void shouldRejectUnsupportedSortField() {
        assertThatThrownBy(() ->
            routeService.listRoutes(
                0,
                20,
                "unknownField,asc"
            )
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(
                "unsupported sort field: unknownField"
            );

        verifyNoInteractions(mockRouteRepository);
    }

    @Test
    void shouldRejectInvalidSortDirection() {
        assertThatThrownBy(() ->
            routeService.listRoutes(
                0,
                20,
                "createdAt,sideways"
            )
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(
                "sort direction must be asc or desc"
            );

        verifyNoInteractions(mockRouteRepository);
    }

    @Test
    void shouldReturnRouteDetails() {

        Route route = createRoute(
            "Passo dello Stelvio",
            "Bormio",
            "Prato allo Stelvio",
            "47.50",
            Difficulty.HARD
        );
        UUID id = route.getId();

        when(mockRouteRepository.findById(id)).thenReturn(Optional.of(route));

        RouteResponse routeResponse = routeService.getRoute(id);

        assertThat(routeResponse.id()).isEqualTo(id);
        assertThat(routeResponse.name()).isEqualTo(route.getName());
        assertThat(routeResponse.description()).isEqualTo(route.getDescription());
        assertThat(routeResponse.startLocation()).isEqualTo(route.getStartLocation());
        assertThat(routeResponse.endLocation()).isEqualTo(route.getEndLocation());
        assertThat(routeResponse.distanceKm()).isEqualTo(route.getDistanceKm());
        assertThat(routeResponse.difficulty()).isEqualTo(route.getDifficulty());
        assertThat(routeResponse.createdAt()).isEqualTo(route.getCreatedAt());
        assertThat(routeResponse.updatedAt()).isEqualTo(route.getUpdatedAt());

        verify(mockRouteRepository).findById(id);
        verifyNoMoreInteractions(mockRouteRepository);
    }

    @Test
    void shouldRejectRequestWhenRouteDoesNotExist() {

        UUID routeId = UUID.randomUUID();

        when(mockRouteRepository.findById(routeId))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            routeService.getRoute(routeId)
        )
            .isInstanceOf(RouteNotFoundException.class)
            .hasMessage("Route not found with id: " + routeId);

        verify(mockRouteRepository).findById(routeId);
        verifyNoMoreInteractions(mockRouteRepository);
    }

    @Test
    void shouldUpdateRoute() {

        Route route = createRoute(
            "Passo dello Stelvio",
            "Bormio",
            "Prato allo Stelvio",
            "47.50",
            Difficulty.HARD
        );

        UUID routeId = route.getId();

        UpdateRouteRequest updateRequest =
            new UpdateRouteRequest(
                "Passo dello Stelvio",
                "  Percorso panoramico  ",
                "Bormio",
                "Prato allo Stelvio",
                new BigDecimal("40.00"),
                Difficulty.MEDIUM
            );

        when(mockRouteRepository.findById(routeId))
            .thenReturn(Optional.of(route));

        RouteResponse routeResponse =
            routeService.updateRoute(
                routeId,
                updateRequest
            );

        assertThat(routeResponse.id())
            .isEqualTo(routeId);

        assertThat(routeResponse.name())
            .isEqualTo("Passo dello Stelvio");

        assertThat(routeResponse.description())
            .isEqualTo("Percorso panoramico");

        assertThat(routeResponse.startLocation())
            .isEqualTo("Bormio");

        assertThat(routeResponse.endLocation())
            .isEqualTo("Prato allo Stelvio");

        assertThat(routeResponse.distanceKm())
            .isEqualByComparingTo("40.00");

        assertThat(routeResponse.difficulty())
            .isEqualTo(Difficulty.MEDIUM);

        assertThat(routeResponse.createdAt())
            .isNotNull();

        assertThat(routeResponse.updatedAt())
            .isNotNull();

        assertThat(routeResponse.updatedAt())
            .isAfterOrEqualTo(routeResponse.createdAt());

        verify(mockRouteRepository).findById(routeId);
        verifyNoMoreInteractions(mockRouteRepository);
    }

    private Route createRoute(
        String name,
        String startLocation,
        String endLocation,
        String distanceKm,
        Difficulty difficulty
    ) {
        return Route.create(
            name,
            "Route description",
            startLocation,
            endLocation,
            new BigDecimal(distanceKm),
            difficulty
        );
    }
}
