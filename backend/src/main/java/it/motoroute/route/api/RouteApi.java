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
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(
    name = "Routes",
    description = "Operations for motorcycle routes"
)
@RequestMapping("/api/routes")
public interface RouteApi {

    @Operation(
        summary = "Create a motorcycle route",
        description = "Creates a new motorcycle route and returns the persisted resource."
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
                examples = @ExampleObject(value = RouteApiExamples.ROUTE_RESPONSE)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request or malformed JSON",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(value = RouteApiExamples.VALIDATION_ERROR)
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
    ResponseEntity<RouteResponse> createRoute(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Data of the motorcycle route to create",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CreateRouteRequest.class),
                examples = @ExampleObject(value = RouteApiExamples.CREATE_REQUEST)
            )
        )
        @Valid @RequestBody CreateRouteRequest request
    );

    @Operation(
        summary = "List motorcycle routes",
        description = "Returns a paginated and sorted list of motorcycle routes. Page numbering starts from zero."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Route page returned successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RoutePageResponse.class),
                examples = @ExampleObject(value = RouteApiExamples.ROUTE_PAGE_RESPONSE)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid pagination or sorting parameters",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(value = RouteApiExamples.INVALID_PAGE_SIZE)
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
    ResponseEntity<RoutePageResponse> listRoutes(
        @Parameter(description = "Zero-based page number", example = "0")
        @RequestParam(defaultValue = "0") int page,

        @Parameter(description = "Number of routes per page, from 1 to 100", example = "20")
        @RequestParam(defaultValue = "20") int size,

        @Parameter(
            description = "Sort property and direction, separated by a comma. Supported properties: createdAt, name, startLocation, endLocation, distanceKm and difficulty.",
            example = "createdAt,desc"
        )
        @RequestParam(defaultValue = "createdAt,desc") String sort
    );

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
                examples = @ExampleObject(value = RouteApiExamples.ROUTE_RESPONSE)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Route ID is not a valid UUID",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(value = RouteApiExamples.INVALID_ROUTE_ID)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Route with the specified ID does not exist",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(value = RouteApiExamples.ROUTE_NOT_FOUND)
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
    ResponseEntity<RouteResponse> getRoute(
        @Parameter(
            description = "Unique identifier of the motorcycle route",
            example = "11111111-1111-1111-1111-111111111111",
            required = true,
            schema = @Schema(type = "string", format = "uuid")
        )
        @PathVariable UUID routeId
    );

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
                examples = @ExampleObject(value = RouteApiExamples.UPDATED_ROUTE_RESPONSE)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid route ID, invalid request body or malformed JSON",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(value = RouteApiExamples.VALIDATION_ERROR)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Route with the specified ID does not exist",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(value = RouteApiExamples.ROUTE_NOT_FOUND)
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
    ResponseEntity<RouteResponse> updateRoute(
        @Parameter(
            description = "Unique identifier of the motorcycle route",
            example = "11111111-1111-1111-1111-111111111111",
            required = true,
            schema = @Schema(type = "string", format = "uuid")
        )
        @PathVariable UUID routeId,

        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Data of the motorcycle route to update",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UpdateRouteRequest.class),
                examples = @ExampleObject(value = RouteApiExamples.UPDATE_REQUEST)
            )
        )
        @Valid @RequestBody UpdateRouteRequest request
    );

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
                examples = @ExampleObject(value = RouteApiExamples.INVALID_ROUTE_ID)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Route with the specified ID does not exist",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiError.class),
                examples = @ExampleObject(value = RouteApiExamples.ROUTE_NOT_FOUND)
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
    ResponseEntity<Void> deleteRoute(
        @Parameter(
            description = "Unique identifier of the motorcycle route",
            example = "11111111-1111-1111-1111-111111111111",
            required = true,
            schema = @Schema(type = "string", format = "uuid")
        )
        @PathVariable UUID routeId
    );
}
