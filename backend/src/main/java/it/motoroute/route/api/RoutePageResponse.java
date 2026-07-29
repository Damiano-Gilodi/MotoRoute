package it.motoroute.route.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(
    description = "Paginated route list"
)
public record RoutePageResponse(

    @Schema(
        description = "Routes contained in the current page"
    )
    List<RouteSummaryResponse> content,

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
        description = "Total number of routes",
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
