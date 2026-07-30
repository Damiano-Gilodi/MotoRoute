package it.motoroute.route.api;

import it.motoroute.common.api.GlobalExceptionHandler;
import it.motoroute.route.application.RouteNotFoundException;
import it.motoroute.route.application.RouteService;
import it.motoroute.route.domain.Difficulty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RouteController.class)
@Import(GlobalExceptionHandler.class)
class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RouteService routeService;

    @Test
    void shouldReturn201AndLocationWhenRequestIsValid() throws Exception {
        UUID routeId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.parse("2026-07-27T09:39:03.514796391Z");

        RouteResponse response = new RouteResponse(
            routeId,
            "Passo dello Stelvio",
            "Percorso panoramico",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD,
            now,
            now
        );

        when(routeService.createRoute(any(CreateRouteRequest.class)))
            .thenReturn(response);

        mockMvc.perform(post("/api/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Passo dello Stelvio",
                      "description": "Percorso panoramico",
                      "startLocation": "Bormio",
                      "endLocation": "Prato allo Stelvio",
                      "distanceKm": 47.50,
                      "difficulty": "HARD"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(header().string(
                "Location",
                "/api/routes/" + routeId
            ))
            .andExpect(jsonPath("$.id").value(routeId.toString()))
            .andExpect(jsonPath("$.name").value("Passo dello Stelvio"))
            .andExpect(jsonPath("$.description").value("Percorso panoramico"))
            .andExpect(jsonPath("$.startLocation").value("Bormio"))
            .andExpect(jsonPath("$.endLocation").value("Prato allo Stelvio"))
            .andExpect(jsonPath("$.distanceKm").value(47.50))
            .andExpect(jsonPath("$.difficulty").value("HARD"))
            .andExpect(jsonPath("$.createdAt").isNotEmpty())
            .andExpect(jsonPath("$.updatedAt").isNotEmpty());

        verify(routeService).createRoute(any(CreateRouteRequest.class));
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "   ",
                      "description": "Percorso panoramico",
                      "startLocation": "Bormio",
                      "endLocation": "Prato allo Stelvio",
                      "distanceKm": 47.50,
                      "difficulty": "HARD"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message")
                .value("One or more fields are invalid"))
            .andExpect(jsonPath("$.path").value("/api/routes"))
            .andExpect(jsonPath("$.fieldErrors.name").exists());

        verifyNoInteractions(routeService);
    }

    @Test
    void shouldReturn400WhenDistanceIsNegative() throws Exception {
        mockMvc.perform(post("/api/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Passo dello Stelvio",
                      "description": "Percorso panoramico",
                      "startLocation": "Bormio",
                      "endLocation": "Prato allo Stelvio",
                      "distanceKm": -1,
                      "difficulty": "HARD"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.distanceKm").exists());

        verifyNoInteractions(routeService);
    }

    @Test
    void shouldReturn400WhenDifficultyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Passo dello Stelvio",
                      "description": "Percorso panoramico",
                      "startLocation": "Bormio",
                      "endLocation": "Prato allo Stelvio",
                      "distanceKm": 47.50,
                      "difficulty": "EXTREME"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message")
                .value("Malformed or invalid JSON request"));

        verifyNoInteractions(routeService);
    }

    @Test
    void shouldReturn400WhenJsonIsMalformed() throws Exception {
        mockMvc.perform(post("/api/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Passo dello Stelvio",
                      "distanceKm":
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message")
                .value("Malformed or invalid JSON request"));

        verifyNoInteractions(routeService);
    }

    @Test
    void shouldReturn200WithDefaultPaginationAndSorting() throws Exception {
        OffsetDateTime createdAt = OffsetDateTime.parse(
            "2026-07-29T10:00:00Z"
        );

        RouteSummaryResponse route = new RouteSummaryResponse(
            UUID.fromString(
                "11111111-1111-1111-1111-111111111111"
            ),
            "Passo dello Stelvio",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD,
            createdAt
        );

        RoutePageResponse response = new RoutePageResponse(
            List.of(route),
            0,
            20,
            1,
            1,
            true,
            true
        );

        when(routeService.listRoutes(
            0,
            20,
            "createdAt,desc"
        )).thenReturn(response);

        mockMvc.perform(get("/api/routes"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(
                MediaType.APPLICATION_JSON
            ))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].id")
                .value("11111111-1111-1111-1111-111111111111"))
            .andExpect(jsonPath("$.content[0].name")
                .value("Passo dello Stelvio"))
            .andExpect(jsonPath("$.content[0].startLocation")
                .value("Bormio"))
            .andExpect(jsonPath("$.content[0].endLocation")
                .value("Prato allo Stelvio"))
            .andExpect(jsonPath("$.content[0].distanceKm")
                .value(47.50))
            .andExpect(jsonPath("$.content[0].difficulty")
                .value("HARD"))
            .andExpect(jsonPath("$.content[0].createdAt")
                .value("2026-07-29T10:00:00Z"))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(20))
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.totalPages").value(1))
            .andExpect(jsonPath("$.first").value(true))
            .andExpect(jsonPath("$.last").value(true));

        verify(routeService).listRoutes(
            0,
            20,
            "createdAt,desc"
        );
    }

    @Test
    void shouldPassCustomPaginationAndSortingToService() throws Exception {
        RoutePageResponse response = new RoutePageResponse(
            List.of(),
            2,
            5,
            10,
            2,
            false,
            true
        );

        when(routeService.listRoutes(
            2,
            5,
            "name,asc"
        )).thenReturn(response);

        mockMvc.perform(get("/api/routes")
                .param("page", "2")
                .param("size", "5")
                .param("sort", "name,asc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isEmpty())
            .andExpect(jsonPath("$.page").value(2))
            .andExpect(jsonPath("$.size").value(5))
            .andExpect(jsonPath("$.totalElements").value(10))
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.first").value(false))
            .andExpect(jsonPath("$.last").value(true));

        verify(routeService).listRoutes(
            2,
            5,
            "name,asc"
        );
    }

    @Test
    void shouldReturn400WhenPageIsNegative() throws Exception {
        when(routeService.listRoutes(
            -1,
            20,
            "createdAt,desc"
        )).thenThrow(
            new IllegalArgumentException(
                "page must be greater than or equal to zero"
            )
        );

        mockMvc.perform(get("/api/routes")
                .param("page", "-1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value(
                "page must be greater than or equal to zero"
            ))
            .andExpect(jsonPath("$.path").value("/api/routes"))
            .andExpect(jsonPath("$.fieldErrors").isEmpty());

        verify(routeService).listRoutes(
            -1,
            20,
            "createdAt,desc"
        );
    }

    @Test
    void shouldReturn200WithEmptyContentWhenNoRoutesExist() throws Exception {

        RoutePageResponse response = new RoutePageResponse(
            List.of(),
            0,
            20,
            0,
            0,
            true,
            true
        );

        when(routeService.listRoutes(
            0,
            20,
            "createdAt,desc"
        )).thenReturn(response);

        mockMvc.perform(get("/api/routes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isEmpty())
            .andExpect(jsonPath("$.totalElements").value(0))
            .andExpect(jsonPath("$.totalPages").value(0))
            .andExpect(jsonPath("$.first").value(true))
            .andExpect(jsonPath("$.last").value(true));

        verify(routeService).listRoutes(
            0,
            20,
            "createdAt,desc"
        );
    }

    @Test
    void shouldReturn400WhenPageSizeExceedsMaximum() throws Exception {

        when(routeService.listRoutes(
            0,
            101,
            "createdAt,desc"
        )).thenThrow(
            new IllegalArgumentException(
                "size must be between 1 and 100"
            )
        );

        mockMvc.perform(get("/api/routes")
                .param("size", "101"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message")
                .value("size must be between 1 and 100"))
            .andExpect(jsonPath("$.path").value("/api/routes"));

        verify(routeService).listRoutes(
            0,
            101,
            "createdAt,desc"
        );
    }

    @Test
    void shouldReturn400WhenSortFieldIsUnsupported() throws Exception {

        when(routeService.listRoutes(
            0,
            20,
            "unknownField,asc"
        )).thenThrow(
            new IllegalArgumentException(
                "unsupported sort field: unknownField"
            )
        );

        mockMvc.perform(get("/api/routes")
                .param("sort", "unknownField,asc"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value(
                "unsupported sort field: unknownField"
            ))
            .andExpect(jsonPath("$.path").value("/api/routes"));

        verify(routeService).listRoutes(
            0,
            20,
            "unknownField,asc"
        );
    }

    @Test
    void shouldReturn400WhenPageIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/routes")
                .param("page", "abc"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value(
                "Parameter 'page' must be of type int"
            ))
            .andExpect(jsonPath("$.path").value("/api/routes"))
            .andExpect(jsonPath("$.fieldErrors").isEmpty());

        verifyNoInteractions(routeService);
    }

    @Test
    void shouldReturn200WithRouteDetailsWhenRouteExists() throws Exception {

        UUID routeId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.parse("2026-07-27T09:39:03.514796391Z");

        RouteResponse routeResponse = new RouteResponse(
            routeId,
            "Passo dello Stelvio",
            "Percorso panoramico",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD,
            now,
            now
        );

        when(routeService.getRoute(routeId)).thenReturn(routeResponse);

        mockMvc.perform(get("/api/routes/" + routeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(routeId.toString()))
            .andExpect(jsonPath("$.name").value("Passo dello Stelvio"))
            .andExpect(jsonPath("$.description").value("Percorso panoramico"))
            .andExpect(jsonPath("$.startLocation").value("Bormio"))
            .andExpect(jsonPath("$.endLocation").value("Prato allo Stelvio"))
            .andExpect(jsonPath("$.distanceKm").value(47.50))
            .andExpect(jsonPath("$.difficulty").value("HARD"))
            .andExpect(jsonPath("$.createdAt").value(now.toString()))
            .andExpect(jsonPath("$.updatedAt").value(now.toString()));

        verify(routeService).getRoute(routeId);
    }

    @Test
    void shouldReturn404WhenRouteDoesNotExist() throws Exception {

        UUID routeId = UUID.randomUUID();

        when(routeService.getRoute(routeId)).thenThrow(new RouteNotFoundException(routeId));

        mockMvc.perform(get("/api/routes/" + routeId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value("Route not found with id: " + routeId))
            .andExpect(jsonPath("$.path").value("/api/routes/" + routeId))
            .andExpect(jsonPath("$.fieldErrors").isEmpty());

        verify(routeService).getRoute(routeId);
    }

    @Test
    void shouldReturn400WhenRouteIdIsInvalid() throws Exception {

        mockMvc.perform(get("/api/routes/abc"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("Parameter 'routeId' must be of type UUID"))
            .andExpect(jsonPath("$.path").value("/api/routes/abc"))
            .andExpect(jsonPath("$.fieldErrors").isEmpty());

        verifyNoInteractions(routeService);
    }
}
