package it.motoroute.route.api;

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
import it.motoroute.route.application.RouteService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Tag(
    name = "Routes",
    description = "Operations for motorcycle routes"
)
@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @Operation(
        summary = "Create a motorcycle route",
        description = """
            Creates a new motorcycle route and returns the persisted resource.
            The Location header contains the URI of the created route.
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Route created successfully",
            headers = @Header(
                name = "Location",
                description = "URI of the created route",
                schema = @Schema(
                    type = "string",
                    example = "/api/routes/11111111-1111-1111-1111-111111111111"
                )
            ),
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RouteResponse.class),
                examples = @ExampleObject(
                    name = "Created route",
                    value = """
                        {
                          "id": "11111111-1111-1111-1111-111111111111",
                          "name": "Passo dello Stelvio",
                          "description": "Percorso panoramico",
                          "startLocation": "Bormio",
                          "endLocation": "Prato allo Stelvio",
                          "distanceKm": 47.50,
                          "difficulty": "HARD",
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
                          "path": "/api/routes",
                          "fieldErrors": {
                            "name": "must not be blank"
                          }
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
    @PostMapping
    public ResponseEntity<RouteResponse> createRoute(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Data of the motorcycle route to create",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CreateRouteRequest.class),
                examples = @ExampleObject(
                    name = "Stelvio route",
                    value = """
                        {
                          "name": "Passo dello Stelvio",
                          "description": "Percorso panoramico",
                          "startLocation": "Bormio",
                          "endLocation": "Prato allo Stelvio",
                          "distanceKm": 47.50,
                          "difficulty": "HARD"
                        }
                        """
                )
            )
        )
        @Valid @RequestBody CreateRouteRequest request
    ) {
        RouteResponse response = routeService.createRoute(request);

        URI location = URI.create("/api/routes/" + response.id());

        return ResponseEntity
            .created(location)
            .body(response);
    }

    @Operation(
        summary = "List motorcycle routes",
        description = """
            Returns a paginated and sorted list of motorcycle routes.
            Page numbering starts from zero.
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Route page returned successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RoutePageResponse.class),
                examples = @ExampleObject(
                    name = "Paginated route list",
                    value = """
                        {
                          "content": [
                            {
                              "id": "11111111-1111-1111-1111-111111111111",
                              "name": "Passo dello Stelvio",
                              "startLocation": "Bormio",
                              "endLocation": "Prato allo Stelvio",
                              "distanceKm": 47.50,
                              "difficulty": "HARD",
                              "createdAt": "2026-07-27T10:00:00Z"
                            }
                          ],
                          "page": 0,
                          "size": 20,
                          "totalElements": 1,
                          "totalPages": 1,
                          "first": true,
                          "last": true
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid pagination or sorting parameters",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(
                    name = "Invalid page size",
                    value = """
                        {
                          "timestamp": "2026-07-29T10:00:00Z",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "size must be between 1 and 100",
                          "path": "/api/routes",
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
    @GetMapping
    public ResponseEntity<RoutePageResponse> listRoutes(

        @Parameter(
            description = "Zero-based page number",
            example = "0"
        )
        @RequestParam(defaultValue = "0")
        int page,

        @Parameter(
            description = "Number of routes per page, from 1 to 100",
            example = "20"
        )
        @RequestParam(defaultValue = "20")
        int size,

        @Parameter(
            description = """
                Sort property and direction, separated by a comma.
                Supported properties: createdAt, name, startLocation,
                endLocation, distanceKm and difficulty.
                """,
            example = "createdAt,desc"
        )
        @RequestParam(defaultValue = "createdAt,desc")
        String sort

    ) {
        RoutePageResponse response = routeService.listRoutes(
            page,
            size,
            sort
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get motorcycle route by ID",
        description = "Returns the details of a motorcycle route by its ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Route details returned successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RouteResponse.class),
                examples = @ExampleObject(
                    name = "Get route details",
                    value = """
                        {
                          "id": "11111111-1111-1111-1111-111111111111",
                          "name": "Passo dello Stelvio",
                          "description": "Percorso panoramico",
                          "startLocation": "Bormio",
                          "endLocation": "Prato allo Stelvio",
                          "distanceKm": 47.50,
                          "difficulty": "HARD",
                          "createdAt": "2026-07-27T10:00:00Z",
                          "updatedAt": "2026-07-27T10:00:00Z"
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
                          "path": "/api/routes/11111111-1111-1111-1111-111111111111",
                          "fieldErrors": {}
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Route ID is not a valid UUID",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(
                    name = "Invalid route id",
                    value = """
                        {
                          "timestamp": "2026-07-29T10:00:00Z",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Parameter 'routeId' must be of type UUID",
                          "path": "/api/routes/abc",
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
    @GetMapping("/{routeId}")
    public ResponseEntity<RouteResponse> getRoute(
        @Parameter(
            description = "Unique identifier of the motorcycle route",
            example = "11111111-1111-1111-1111-111111111111",
            required = true,
            schema = @Schema(
                type = "string",
                format = "uuid"
            )
        )
        @PathVariable UUID routeId
    ) {

        RouteResponse response = routeService.getRoute(routeId);

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Update a motorcycle route",
        description = "Updates an existing motorcycle route and returns its details."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Route updated successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RouteResponse.class),
                examples = @ExampleObject(
                    name = "Updated route",
                    value = """
                        {
                          "id": "11111111-1111-1111-1111-111111111111",
                          "name": "Passo dello Stelvio",
                          "description": "Percorso panoramico",
                          "startLocation": "Bormio",
                          "endLocation": "Prato allo Stelvio",
                          "distanceKm": 40.00,
                          "difficulty": "MEDIUM"
                          "createdAt": "2026-07-27T10:00:00Z",
                          "updatedAt": "2026-09-08T15:30:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid route ID, invalid request body or malformed JSON",
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
                          "path": "/api/routes/11111111-1111-1111-1111-111111111111",
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
                          "path": "/api/routes/11111111-1111-1111-1111-111111111111",
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
    @PutMapping("/{routeId}")
    public ResponseEntity<RouteResponse> updateRoute(
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
            required = true,
            description = "Data of the motorcycle route to update",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UpdateRouteRequest.class),
                examples = @ExampleObject(
                    name = "Stelvio route",
                    value = """
                        {
                          "name": "Passo dello Stelvio",
                          "description": "Percorso panoramico",
                          "startLocation": "Bormio",
                          "endLocation": "Prato allo Stelvio",
                          "distanceKm": 40.00,
                          "difficulty": "MEDIUM"
                        }
                        """
                )
            )
        )
        @Valid @RequestBody UpdateRouteRequest request
    ) {
        RouteResponse response =
            routeService.updateRoute(routeId, request);

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Delete a motorcycle route",
        description = "Deletes an existing motorcycle route."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Route deleted successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Route ID is not a valid UUID",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(
                    name = "Invalid route id",
                    value = """
                        {
                          "timestamp": "2026-07-29T10:00:00Z",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Parameter 'routeId' must be of type UUID",
                          "path": "/api/routes/abc",
                          "fieldErrors": {}
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
                          "path": "/api/routes/11111111-1111-1111-1111-111111111111",
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
    @DeleteMapping("/{routeId}")
    public ResponseEntity<Void> deleteRoute(
        @Parameter(
            description = "Unique identifier of the motorcycle route",
            example = "11111111-1111-1111-1111-111111111111",
            required = true,
            schema = @Schema(
                type = "string",
                format = "uuid"
            )
        )
        @PathVariable UUID routeId
    ) {

        routeService.deleteRoute(routeId);

        return ResponseEntity.noContent().build();
    }
}
