package it.motoroute.route.api;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

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
}
