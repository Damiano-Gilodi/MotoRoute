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
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(
    name = "Waypoints",
    description = "Operations for motorcycle waypoints"
)
@RequestMapping("/api/routes/{routeId}/waypoints")
public interface WaypointApi {

    @Operation(
        summary = "Create a new waypoint",
        description = "Creates a waypoint belonging to the specified motorcycle route"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Waypoint created successfully",
            headers = @Header(
                name = "Location",
                description = "URI of the created waypoint",
                schema = @Schema(type = "string")
            ),
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = WaypointResponse.class),
                examples = @ExampleObject(value = WaypointApiExamples.WAYPOINT_RESPONSE)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request or malformed JSON",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(value = WaypointApiExamples.VALIDATION_ERROR)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Route with the specified ID does not exist",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(value = WaypointApiExamples.ROUTE_NOT_FOUND)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Waypoint position already occupied in the specified route",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(value = WaypointApiExamples.POSITION_CONFLICT)
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
    @PostMapping
    ResponseEntity<WaypointResponse> createWaypoint(
        @Parameter(
            description = "Unique identifier of the motorcycle route",
            required = true,
            schema = @Schema(type = "string", format = "uuid")
        )
        @PathVariable UUID routeId,

        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Data of the motorcycle waypoint to create",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CreateWaypointRequest.class),
                examples = @ExampleObject(value = WaypointApiExamples.CREATE_REQUEST)
            )
        )
        @Valid @RequestBody CreateWaypointRequest request
    );

    @Operation(
        summary = "List route waypoints",
        description = "Returns a paginated list of waypoints ordered by position"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "List of waypoints in the specified route",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = WaypointPageResponse.class),
                examples = @ExampleObject(value = WaypointApiExamples.WAYPOINT_PAGE_RESPONSE)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid page or size parameters",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Route with the specified ID does not exist",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(value = WaypointApiExamples.ROUTE_NOT_FOUND)
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
    @GetMapping
    ResponseEntity<WaypointPageResponse> listWaypoints(
        @Parameter(
            description = "Unique identifier of the motorcycle route",
            required = true,
            schema = @Schema(type = "string", format = "uuid")
        )
        @PathVariable UUID routeId,

        @Parameter(description = "Zero-based page number", example = "0")
        @RequestParam(defaultValue = "0") int page,

        @Parameter(description = "Number of waypoints per page, from 1 to 100", example = "20")
        @RequestParam(defaultValue = "20") int size
    );

    @Operation(
        summary = "Get a waypoint",
        description = "Returns a waypoint belonging to the specified motorcycle route"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Waypoint found successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = WaypointResponse.class),
                examples = @ExampleObject(value = WaypointApiExamples.WAYPOINT_RESPONSE)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid route or waypoint identifier",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Route or waypoint with the specified ID does not exist",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(value = WaypointApiExamples.WAYPOINT_NOT_FOUND)
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
    @GetMapping("/{waypointId}")
    ResponseEntity<WaypointResponse> getWaypoint(
        @Parameter(
            description = "Unique identifier of the motorcycle route",
            required = true,
            schema = @Schema(type = "string", format = "uuid")
        )
        @PathVariable UUID routeId,

        @Parameter(
            description = "Unique identifier of the waypoint",
            required = true,
            schema = @Schema(type = "string", format = "uuid")
        )
        @PathVariable UUID waypointId
    );

    @Operation(
        summary = "Update a waypoint",
        description = "Updates an existing waypoint belonging to the specified motorcycle route"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Waypoint updated successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = WaypointResponse.class),
                examples = @ExampleObject(value = WaypointApiExamples.WAYPOINT_RESPONSE)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request or malformed JSON",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(value = WaypointApiExamples.VALIDATION_ERROR)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Route or waypoint with the specified ID does not exist",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(value = WaypointApiExamples.WAYPOINT_NOT_FOUND)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Waypoint position already occupied in the specified route",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(value = WaypointApiExamples.POSITION_CONFLICT)
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
    @PutMapping("/{waypointId}")
    ResponseEntity<WaypointResponse> updateWaypoint(
        @Parameter(
            description = "Unique identifier of the motorcycle route",
            required = true,
            schema = @Schema(type = "string", format = "uuid")
        )
        @PathVariable UUID routeId,

        @Parameter(
            description = "Unique identifier of the waypoint",
            required = true,
            schema = @Schema(type = "string", format = "uuid")
        )
        @PathVariable UUID waypointId,

        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Data of the motorcycle waypoint to update",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UpdateWaypointRequest.class),
                examples = @ExampleObject(value = WaypointApiExamples.UPDATE_REQUEST)
            )
        )
        @Valid @RequestBody UpdateWaypointRequest request
    );
}
