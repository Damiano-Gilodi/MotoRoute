package it.motoroute.waypoint.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(
    description = "Paginated list of waypoints"
)
public record WaypointPageResponse(

    @Schema(
        description = "List of waypoints in the current page"
    )
    List<WaypointSummaryResponse> content,

    @Schema(
        description = "Current zero-based page number",
        example = "0"
    )
    int page,

    @Schema(
        description = "Requested page size",
        example = "20"
    )
    int size,

    @Schema(
        description = "Total number of waypoints",
        example = "45"
    )
    long totalElements,

    @Schema(
        description = "Total number of pages",
        example = "3"
    )
    int totalPages,

    @Schema(
        description = "Whether this is the first page",
        example = "true"
    )
    boolean first,

    @Schema(
        description = "Whether this is the last page",
        example = "false"
    )
    boolean last
) {
}
