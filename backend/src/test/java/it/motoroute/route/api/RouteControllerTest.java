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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RouteController.class)
@Import(GlobalExceptionHandler.class)
class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RouteService routeService;

    private static final UUID ROUTE_ID = UUID.fromString(
        "11111111-1111-1111-1111-111111111111"
    );

    private static final OffsetDateTime CREATED_AT = OffsetDateTime.parse(
        "2026-07-29T10:00:00Z"
    );

    private static final OffsetDateTime UPDATED_AT = OffsetDateTime.parse(
        "2026-07-29T10:30:00Z"
    );

    private static final RouteSummaryResponse ROUTE_SUMMARY =
        new RouteSummaryResponse(
            ROUTE_ID,
            "Passo dello Stelvio",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD,
            CREATED_AT
        );

    private static final String VALID_CREATE_JSON = """
        {
          "name": "Passo dello Stelvio",
          "description": "Percorso panoramico",
          "startLocation": "Bormio",
          "endLocation": "Prato allo Stelvio",
          "distanceKm": 47.50,
          "difficulty": "HARD"
        }
        """;

    private static final String VALID_UPDATE_JSON = """
        {
          "name": "Passo dello Stelvio",
          "description": "Percorso panoramico",
          "startLocation": "Bormio",
          "endLocation": "Prato allo Stelvio",
          "distanceKm": 40.00,
          "difficulty": "MEDIUM"
        }
        """;

    @Test
    void shouldReturn201AndLocationWhenRequestIsValid() throws Exception {
        RouteResponse response = new RouteResponse(
            ROUTE_ID,
            "Passo dello Stelvio",
            "Percorso panoramico",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD,
            CREATED_AT,
            CREATED_AT
        );

        when(routeService.createRoute(any(CreateRouteRequest.class)))
            .thenReturn(response);

        mockMvc.perform(post("/api/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_CREATE_JSON))
            .andExpect(status().isCreated())
            .andExpect(header().string(
                "Location",
                "/api/routes/" + ROUTE_ID
            ))
            .andExpect(jsonPath("$.id").value(ROUTE_ID.toString()))
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
        RoutePageResponse response = new RoutePageResponse(
            List.of(ROUTE_SUMMARY),
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
            List.of(ROUTE_SUMMARY),
            1,
            5,
            6,
            2,
            false,
            true
        );

        when(routeService.listRoutes(
            1,
            5,
            "name,asc"
        )).thenReturn(response);

        mockMvc.perform(get("/api/routes")
                .param("page", "1")
                .param("size", "5")
                .param("sort", "name,asc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.size").value(5))
            .andExpect(jsonPath("$.totalElements").value(6))
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.first").value(false))
            .andExpect(jsonPath("$.last").value(true));

        verify(routeService).listRoutes(
            1,
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

        RouteResponse routeResponse = new RouteResponse(
            ROUTE_ID,
            "Passo dello Stelvio",
            "Percorso panoramico",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD,
            CREATED_AT,
            CREATED_AT
        );

        when(routeService.getRoute(ROUTE_ID)).thenReturn(routeResponse);

        mockMvc.perform(get("/api/routes/{routeId}", ROUTE_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(ROUTE_ID.toString()))
            .andExpect(jsonPath("$.name").value("Passo dello Stelvio"))
            .andExpect(jsonPath("$.description").value("Percorso panoramico"))
            .andExpect(jsonPath("$.startLocation").value("Bormio"))
            .andExpect(jsonPath("$.endLocation").value("Prato allo Stelvio"))
            .andExpect(jsonPath("$.distanceKm").value(47.50))
            .andExpect(jsonPath("$.difficulty").value("HARD"))
            .andExpect(jsonPath("$.createdAt").value("2026-07-29T10:00:00Z"))
            .andExpect(jsonPath("$.updatedAt").value("2026-07-29T10:00:00Z"));

        verify(routeService).getRoute(ROUTE_ID);
    }

    @Test
    void shouldReturn404WhenRouteDoesNotExist() throws Exception {

        when(routeService.getRoute(ROUTE_ID))
            .thenThrow(new RouteNotFoundException(ROUTE_ID));

        mockMvc.perform(get("/api/routes/{routeId}", ROUTE_ID))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value("Route not found with id: " + ROUTE_ID))
            .andExpect(jsonPath("$.path").value("/api/routes/" + ROUTE_ID))
            .andExpect(jsonPath("$.fieldErrors").isEmpty());

        verify(routeService).getRoute(ROUTE_ID);
    }

    @Test
    void shouldReturn400WhenRouteIdIsInvalid() throws Exception {

        mockMvc.perform(get("/api/routes/{routeId}", "abc"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("Parameter 'routeId' must be of type UUID"))
            .andExpect(jsonPath("$.path").value("/api/routes/" + "abc"))
            .andExpect(jsonPath("$.fieldErrors").isEmpty());

        verifyNoInteractions(routeService);
    }

    @Test
    void shouldReturn200WhenUpdateRouteIsSuccessful() throws Exception {
        UpdateRouteRequest request = new UpdateRouteRequest(
            "Passo dello Stelvio",
            "Percorso panoramico",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("40.00"),
            Difficulty.MEDIUM
        );

        RouteResponse response = new RouteResponse(
            ROUTE_ID,
            "Passo dello Stelvio",
            "Percorso panoramico",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("40.00"),
            Difficulty.MEDIUM,
            CREATED_AT,
            UPDATED_AT
        );

        when(routeService.updateRoute(ROUTE_ID, request))
            .thenReturn(response);

        mockMvc.perform(put("/api/routes/{routeId}", ROUTE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(VALID_UPDATE_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(
                MediaType.APPLICATION_JSON
            ))
            .andExpect(jsonPath("$.id").value(ROUTE_ID.toString()))
            .andExpect(jsonPath("$.name").value("Passo dello Stelvio"))
            .andExpect(jsonPath("$.description").value("Percorso panoramico"))
            .andExpect(jsonPath("$.startLocation").value("Bormio"))
            .andExpect(jsonPath("$.endLocation").value("Prato allo Stelvio"))
            .andExpect(jsonPath("$.distanceKm").value(40.00))
            .andExpect(jsonPath("$.difficulty").value("MEDIUM"))
            .andExpect(jsonPath("$.createdAt").value("2026-07-29T10:00:00Z"))
            .andExpect(jsonPath("$.updatedAt").value("2026-07-29T10:30:00Z"));

        verify(routeService).updateRoute(ROUTE_ID, request);
    }

    @Test
    void shouldReturn404WhenUpdatingMissingRoute() throws Exception {
        when(routeService.updateRoute(
            eq(ROUTE_ID),
            any(UpdateRouteRequest.class)
        )).thenThrow(
            new RouteNotFoundException(ROUTE_ID)
        );

        mockMvc.perform(
                put("/api/routes/{routeId}", ROUTE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(VALID_UPDATE_JSON)
            )
            .andExpect(status().isNotFound())
            .andExpect(content().contentTypeCompatibleWith(
                MediaType.APPLICATION_JSON
            ))
            .andExpect(jsonPath("$.status")
                .value(404))
            .andExpect(jsonPath("$.error")
                .value("Not Found"))
            .andExpect(jsonPath("$.message")
                .value(
                    "Route not found with id: " + ROUTE_ID
                ))
            .andExpect(jsonPath("$.path")
                .value("/api/routes/" + ROUTE_ID))
            .andExpect(jsonPath("$.fieldErrors")
                .isEmpty());

        verify(routeService).updateRoute(
            eq(ROUTE_ID),
            any(UpdateRouteRequest.class)
        );
    }

    @Test
    void shouldReturn400WhenUpdateRequestIsInvalid() throws Exception {
        mockMvc.perform(
                put("/api/routes/{routeId}", ROUTE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "name": "   ",
                          "description": "Percorso panoramico",
                          "startLocation": "",
                          "endLocation": "Prato allo Stelvio",
                          "distanceKm": 0,
                          "difficulty": null
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(
                MediaType.APPLICATION_JSON
            ))
            .andExpect(jsonPath("$.status")
                .value(400))
            .andExpect(jsonPath("$.error")
                .value("Bad Request"))
            .andExpect(jsonPath("$.message")
                .value("One or more fields are invalid"))
            .andExpect(jsonPath("$.path")
                .value("/api/routes/" + ROUTE_ID))
            .andExpect(jsonPath("$.fieldErrors.name")
                .exists())
            .andExpect(jsonPath("$.fieldErrors.startLocation")
                .exists())
            .andExpect(jsonPath("$.fieldErrors.distanceKm")
                .exists())
            .andExpect(jsonPath("$.fieldErrors.difficulty")
                .exists());

        verifyNoInteractions(routeService);
    }

    @Test
    void shouldReturn400WhenUpdateRouteIdIsInvalid() throws Exception {
        mockMvc.perform(
                put("/api/routes/{routeId}", "not-a-uuid")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(VALID_UPDATE_JSON)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(
                MediaType.APPLICATION_JSON
            ))
            .andExpect(jsonPath("$.status")
                .value(400))
            .andExpect(jsonPath("$.error")
                .value("Bad Request"))
            .andExpect(jsonPath("$.message")
                .value(
                    "Parameter 'routeId' must be of type UUID"
                ))
            .andExpect(jsonPath("$.path")
                .value("/api/routes/" + "not-a-uuid"))
            .andExpect(jsonPath("$.fieldErrors")
                .isEmpty());

        verifyNoInteractions(routeService);
    }

    @Test
    void shouldReturn400WhenUpdateRequestContainsMalformedJson()
        throws Exception {

        mockMvc.perform(
                put("/api/routes/{routeId}", ROUTE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "name": "Passo dello Stelvio",
                          "description": "Percorso panoramico",
                          "startLocation": "Bormio",
                          "endLocation": "Prato allo Stelvio",
                          "distanceKm": 47.50,
                          "difficulty": "HARD"
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(
                MediaType.APPLICATION_JSON
            ))
            .andExpect(jsonPath("$.status")
                .value(400))
            .andExpect(jsonPath("$.error")
                .value("Bad Request"))
            .andExpect(jsonPath("$.path")
                .value("/api/routes/" + ROUTE_ID));

        verifyNoInteractions(routeService);
    }

    @Test
    void shouldReturn400WhenUpdateDifficultyIsInvalid() throws Exception {
        mockMvc.perform(
                put("/api/routes/{routeId}", ROUTE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "name": "Passo dello Stelvio",
                          "description": "Percorso panoramico",
                          "startLocation": "Bormio",
                          "endLocation": "Prato allo Stelvio",
                          "distanceKm": 47.50,
                          "difficulty": "EXTREME"
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status")
                .value(400))
            .andExpect(jsonPath("$.error")
                .value("Bad Request"));

        verifyNoInteractions(routeService);
    }
}
