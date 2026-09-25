package it.motoroute.waypoint.api;

import it.motoroute.common.api.GlobalExceptionHandler;
import it.motoroute.route.application.RouteNotFoundException;
import it.motoroute.waypoint.application.WaypointPositionConflictException;
import it.motoroute.waypoint.application.WaypointService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WaypointController.class)
@Import(GlobalExceptionHandler.class)
public class WaypointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WaypointService waypointService;

    private static final UUID ROUTE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID WAYPOINT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final OffsetDateTime CREATED_AT = OffsetDateTime.parse("2026-07-29T10:00:00Z");


    private static final String VALID_CREATE_JSON = """
        {
          "name": "Punto panoramico",
          "description": "Belvedere con vista sulle montagne",
          "position": 1,
          "latitude": 45.123456,
          "longitude": 8.765432
        }
        """;

    @Test
    void shouldReturn201AndLocationWhenRequestIsValid() throws Exception {

        WaypointResponse response = new WaypointResponse(
            WAYPOINT_ID,
            ROUTE_ID,
            "Punto panoramico",
            "Belvedere con vista sulle montagne",
            1,
            new BigDecimal("45.123456"),
            new BigDecimal("8.765432"),
            CREATED_AT,
            CREATED_AT
        );

        when(waypointService.createWaypoint(eq(ROUTE_ID), any(CreateWaypointRequest.class)))
            .thenReturn(response);

        mockMvc.perform(post("/api/routes/{routeId}/waypoints", ROUTE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_CREATE_JSON))
            .andExpect(status().isCreated())
            .andExpect(header().string(
                "Location",
                "/api/routes/" + ROUTE_ID + "/waypoints/" + WAYPOINT_ID
            ))
            .andExpect(jsonPath("$.id").value(WAYPOINT_ID.toString()))
            .andExpect(jsonPath("$.routeId").value(ROUTE_ID.toString()))
            .andExpect(jsonPath("$.name").value("Punto panoramico"))
            .andExpect(jsonPath("$.description").value("Belvedere con vista sulle montagne"))
            .andExpect(jsonPath("$.position").value(1))
            .andExpect(jsonPath("$.latitude").value(45.123456))
            .andExpect(jsonPath("$.longitude").value(8.765432))
            .andExpect(jsonPath("$.createdAt").isNotEmpty())
            .andExpect(jsonPath("$.updatedAt").isNotEmpty());

        verify(waypointService).createWaypoint(eq(ROUTE_ID), any(CreateWaypointRequest.class));
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {

        mockMvc.perform(post("/api/routes/{routeId}/waypoints", ROUTE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": " ",
                        "description": "Belvedere con vista sulle montagne",
                        "position": 1,
                        "latitude": 45.123456,
                        "longitude": 8.765432
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("One or more fields are invalid"))
            .andExpect(jsonPath("$.path").value("/api/routes/11111111-1111-1111-1111-111111111111/waypoints"))
            .andExpect(jsonPath("$.fieldErrors.name").exists());

        verifyNoInteractions(waypointService);
    }

    @Test
    void shouldReturn400WhenRouteIdIsInvalid() throws Exception {

        mockMvc.perform(post("/api/routes/{routeId}/waypoints", "invalid-uuid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_CREATE_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("Parameter 'routeId' must be of type UUID"))
            .andExpect(jsonPath("$.path").value("/api/routes/invalid-uuid/waypoints"))
            .andExpect(jsonPath("$.fieldErrors").isEmpty());

        verifyNoInteractions(waypointService);
    }

    @Test
    void shouldReturn400WhenJsonIsMalformed() throws Exception {
        mockMvc.perform(post("/api/routes/{routeId}/waypoints", ROUTE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Punto panoramico",
                        "description": "Belvedere con vista sulle montagne",
                        "position":
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("Malformed or invalid JSON request"))
            .andExpect(jsonPath("$.path").value("/api/routes/11111111-1111-1111-1111-111111111111/waypoints"))
            .andExpect(jsonPath("$.fieldErrors").isEmpty());

        verifyNoInteractions(waypointService);
    }

    @Test
    void shouldReturn404WhenRouteDoesNotExist() throws Exception {

        when(waypointService.createWaypoint(eq(ROUTE_ID), any(CreateWaypointRequest.class)))
            .thenThrow(new RouteNotFoundException(ROUTE_ID));

        mockMvc.perform(post("/api/routes/{routeId}/waypoints", ROUTE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_CREATE_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value("Route not found with id: " + ROUTE_ID))
            .andExpect(jsonPath("$.path").value("/api/routes/11111111-1111-1111-1111-111111111111/waypoints"))
            .andExpect(jsonPath("$.fieldErrors").isEmpty());

        verify(waypointService).createWaypoint(eq(ROUTE_ID), any(CreateWaypointRequest.class));
        verifyNoMoreInteractions(waypointService);
    }

    @Test
    void shouldReturn409WhenWaypointPositionIsOccupied() throws Exception {

        when(waypointService.createWaypoint(eq(ROUTE_ID), any(CreateWaypointRequest.class)))
            .thenThrow(new WaypointPositionConflictException(ROUTE_ID, 1));

        mockMvc.perform(post("/api/routes/{routeId}/waypoints", ROUTE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_CREATE_JSON))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.error").value("Conflict"))
            .andExpect(jsonPath("$.message").value("Waypoint position " + 1 + " already exists in route: " + ROUTE_ID))
            .andExpect(jsonPath("$.path").value("/api/routes/11111111-1111-1111-1111-111111111111/waypoints"))
            .andExpect(jsonPath("$.fieldErrors").isEmpty());

        verify(waypointService).createWaypoint(eq(ROUTE_ID), any(CreateWaypointRequest.class));
        verifyNoMoreInteractions(waypointService);
    }

    @Test
    void shouldReturn500WhenUnexpectedErrorOccurs() throws Exception {

        when(waypointService.createWaypoint(eq(ROUTE_ID), any(CreateWaypointRequest.class)))
            .thenThrow(new RuntimeException());

        mockMvc.perform(post("/api/routes/{routeId}/waypoints", ROUTE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_CREATE_JSON))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.error").value("Internal Server Error"))
            .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
            .andExpect(jsonPath("$.path").value("/api/routes/11111111-1111-1111-1111-111111111111/waypoints"))
            .andExpect(jsonPath("$.fieldErrors").isEmpty());

        verify(waypointService).createWaypoint(eq(ROUTE_ID), any(CreateWaypointRequest.class));
        verifyNoMoreInteractions(waypointService);
    }
}
