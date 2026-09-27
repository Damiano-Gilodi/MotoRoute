package it.motoroute.waypoint.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.motoroute.common.api.ApiError;
import it.motoroute.waypoint.application.WaypointService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Tag(
    name = "Waypoints",
    description = "Operations for motorcycle waypoints"
)
@RestController
@RequestMapping("/api/routes/{routeId}/waypoints")
public class WaypointController {

    private final WaypointService waypointService;

    public WaypointController(WaypointService waypointService) {
        this.waypointService = waypointService;
    }

    @PostMapping
    @Operation(
        summary = "Create a new waypoint",
        description = "Create a new waypoint for a motorcycle route"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Waypoint created successfully",
            headers = @Header(
                name = "Location",
                description = "URI of the created waypoint",
                schema = @Schema(
                    type = "string",
                    example = "/api/routes/11111111-1111-1111-1111-111111111111/waypoints/22222222-2222-2222-2222-222222222222"
                )
            ),
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = WaypointResponse.class),
                examples = @ExampleObject(
                    name = "Created waypoint",
                    value = """
                        {
                        "id": "22222222-2222-2222-2222-222222222222",
                        "routeId": "11111111-1111-1111-1111-111111111111",
                        "name": "Punto panoramico",
                        "description": "Belvedere con vista sulle montagne",
                        "position": 1,
                        "latitude": 45.123456,
                        "longitude": 8.765432,
                        "createdAt": "2026-07-27T10:00:00Z",
                        "updatedAt": "2026-07-27T10:00:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request or malformed JSON",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(
                    name = "Validation error",
                    value = """
                        {
                          "timestamp": "2026-07-27T10:00:00Z",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "One or more fields are invalid",
                          "path": "/api/routes/11111111-1111-1111-1111-111111111111/waypoints",
                          "fieldErrors": {
                            "name": "must not be blank"
                          }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Route with the specified ID does not exist",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(
                    name = "Route not found",
                    value = """
                        {
                          "timestamp": "2026-07-29T10:00:00Z",
                          "status": 404,
                          "error": "Not Found",
                          "message": "Route not found with id: 11111111-1111-1111-1111-111111111111",
                          "path": "/api/routes/11111111-1111-1111-1111-111111111111/waypoints",
                          "fieldErrors": {}
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Waypoint position already occupied in the specified route",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(
                    name = "Waypoint position already occupied",
                    value = """
                        {
                          "timestamp": "2026-07-29T10:00:00Z",
                          "status": 409,
                          "error": "Conflict",
                          "message": "Waypoint position 1 already exists in route: 11111111-1111-1111-1111-111111111111",
                          "path": "/api/routes/11111111-1111-1111-1111-111111111111/waypoints",
                          "fieldErrors": {}
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Unexpected internal server error",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class)
            )
        )
    })
    public ResponseEntity<WaypointResponse> createWaypoint(
        @Parameter(
            description = "Unique identifier of the motorcycle route",
            example = "11111111-1111-1111-1111-111111111111",
            required = true,
            schema = @Schema(
                type = "string",
                format = "uuid"
            )
        )
        @PathVariable UUID routeId,

        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Data of the motorcycle waypoint to create",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CreateWaypointRequest.class),
                examples = @ExampleObject(
                    name = "Waypoint sul percorso",
                    value = """
                        {
                          "name": "Punto panoramico",
                          "description": "Belvedere con vista sulle montagne",
                          "position": 1,
                          "latitude": 45.123456,
                          "longitude": 8.765432
                        }
                        """
                )
            )
        )
        @Valid @RequestBody CreateWaypointRequest request
    ) {

        WaypointResponse response = waypointService.createWaypoint(routeId, request);

        URI location = URI.create("/api/routes/" + routeId + "/waypoints/" + response.id());

        return ResponseEntity.created(location).body(response);
    }
}
