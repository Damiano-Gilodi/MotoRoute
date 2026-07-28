package it.motoroute.route.application;

import it.motoroute.route.api.CreateRouteRequest;
import it.motoroute.route.api.RouteResponse;
import it.motoroute.route.domain.Difficulty;
import it.motoroute.route.domain.Route;
import it.motoroute.route.infrastructure.RouteRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RouteServiceTest {

    @Test
    void shouldCreateRoute() {

        RouteMapper routeMapper = new RouteMapper();
        RouteRepository mockRouteRepository = mock(RouteRepository.class);
        RouteService routeService = new RouteService(routeMapper, mockRouteRepository);

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

        RouteResponse routeResponse = routeService.createRoute(request);

        assertThat(routeResponse.id()).isNotNull();
        assertThat(routeResponse.name()).isEqualTo("Passo dello Stelvio");
        assertThat(routeResponse.description()).isEqualTo("Percorso panoramico");
        assertThat(routeResponse.startLocation()).isEqualTo("Bormio");
        assertThat(routeResponse.endLocation()).isEqualTo("Prato allo Stelvio");
        assertThat(routeResponse.distanceKm()).isEqualByComparingTo("47.50");
        assertThat(routeResponse.difficulty()).isEqualTo(Difficulty.HARD);
        assertThat(routeResponse.createdAt()).isNotNull();
        assertThat(routeResponse.updatedAt()).isNotNull();

        verify(mockRouteRepository, times(1)).save(any(Route.class));
        verifyNoMoreInteractions(mockRouteRepository);
    }
}
