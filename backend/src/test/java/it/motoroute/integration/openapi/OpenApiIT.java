package it.motoroute.integration.openapi;

import it.motoroute.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class OpenApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldExposeCreateRouteOpenApiDocumentation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(
                "application/json"
            ))
            .andExpect(jsonPath("$.info.title")
                .value("MotoRoute API"))
            .andExpect(jsonPath("$.info.version")
                .value("0.1.0"))
            .andExpect(jsonPath("$.paths['/api/routes'].post")
                .exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes'].post.responses['201']"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes'].post.responses['400']"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes'].post.responses['500']"
            ).exists());
    }

    @Test
    void shouldExposeGetRouteDetailsOpenApiDocumentation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(
                "application/json"
            ))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].get"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].get.parameters[0].name"
            ).value("routeId"))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].get.parameters[0].in"
            ).value("path"))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].get.parameters[0].required"
            ).value(true))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].get.parameters[0].schema.type"
            ).value("string"))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].get.parameters[0].schema.format"
            ).value("uuid"))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].get.responses['200']"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].get.responses['400']"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].get.responses['404']"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].get.responses['500']"
            ).exists());
    }

    @Test
    void shouldExposeUpdateRouteOpenApiDocumentation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].put"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].put.parameters[0].name"
            ).value("routeId"))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].put.parameters[0].in"
            ).value("path"))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].put.parameters[0].required"
            ).value(true))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].put.parameters[0].schema.type"
            ).value("string"))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].put.parameters[0].schema.format"
            ).value("uuid"))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].put.requestBody"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].put.responses['200']"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].put.responses['400']"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].put.responses['404']"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].put.responses['500']"
            ).exists());
    }

    @Test
    void shouldExposeDeleteRouteOpenApiDocumentation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].delete"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].delete.parameters[0].name"
            ).value("routeId"))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].delete.parameters[0].in"
            ).value("path"))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].delete.parameters[0].required"
            ).value(true))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].delete.parameters[0].schema.type"
            ).value("string"))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].delete.parameters[0].schema.format"
            ).value("uuid"))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].delete.responses['204']"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].delete.responses['400']"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].delete.responses['404']"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}'].delete.responses['500']"
            ).exists());
    }

    @Test
    void shouldExposeListRoutesOpenApiDocumentation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(
                "$.paths['/api/routes'].get"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes'].get.responses['200']"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes'].get.responses['400']"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes'].get.responses['500']"
            ).exists());
    }

    @Test
    void shouldExposeCreateWaypointOpenApiDocumentation() throws Exception {

        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(
                "application/json"
            ))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}/waypoints'].post"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}/waypoints'].post.parameters[0].name"
            ).value("routeId"))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}/waypoints'].post.parameters[0].in"
            ).value("path"))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}/waypoints'].post.parameters[0].required"
            ).value(true))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}/waypoints'].post.parameters[0].schema.type"
            ).value("string"))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}/waypoints'].post.parameters[0].schema.format"
            ).value("uuid"))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}/waypoints'].post.requestBody"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}/waypoints'].post.requestBody.required"
            ).value(true))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}/waypoints'].post.requestBody.content['application/json'].schema['$ref']"
            ).value("#/components/schemas/CreateWaypointRequest"))
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}/waypoints'].post.responses['201']"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}/waypoints'].post.responses['400']"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}/waypoints'].post.responses['404']"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}/waypoints'].post.responses['409']"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}/waypoints'].post.responses['500']"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}/waypoints'].post.responses['201'].headers.Location"
            ).exists())
            .andExpect(jsonPath(
                "$.paths['/api/routes/{routeId}/waypoints'].post.responses['201'].content['application/json'].schema['$ref']"
            ).value("#/components/schemas/WaypointResponse"))
            .andExpect(jsonPath(
                "$.components.schemas.CreateWaypointRequest"
            ).exists())
            .andExpect(jsonPath(
                "$.components.schemas.WaypointResponse"
            ).exists());
    }

    @Test
    void shouldExposeListWaypointsOpenApiDocumentation() throws Exception {
        String endpoint = "$.paths['/api/routes/{routeId}/waypoints'].get";

        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath(endpoint).exists())
            .andExpect(jsonPath(
                endpoint + ".parameters[?(@.name == 'routeId')].in"
            ).value(hasItem("path")))
            .andExpect(jsonPath(
                endpoint + ".parameters[?(@.name == 'routeId')].required"
            ).value(hasItem(true)))
            .andExpect(jsonPath(
                endpoint + ".parameters[?(@.name == 'routeId')].schema.type"
            ).value(hasItem("string")))
            .andExpect(jsonPath(
                endpoint + ".parameters[?(@.name == 'routeId')].schema.format"
            ).value(hasItem("uuid")))
            .andExpect(jsonPath(
                endpoint + ".parameters[?(@.name == 'page')].in"
            ).value(hasItem("query")))
            .andExpect(jsonPath(
                endpoint + ".parameters[?(@.name == 'page')].schema.type"
            ).value(hasItem("integer")))
            .andExpect(jsonPath(
                endpoint + ".parameters[?(@.name == 'size')].in"
            ).value(hasItem("query")))
            .andExpect(jsonPath(
                endpoint + ".parameters[?(@.name == 'size')].schema.type"
            ).value(hasItem("integer")))
            .andExpect(jsonPath(endpoint + ".responses['200']").exists())
            .andExpect(jsonPath(endpoint + ".responses['400']").exists())
            .andExpect(jsonPath(endpoint + ".responses['404']").exists())
            .andExpect(jsonPath(endpoint + ".responses['500']").exists())
            .andExpect(jsonPath(
                endpoint + ".responses['200'].content['application/json'].schema['$ref']"
            ).value("#/components/schemas/WaypointPageResponse"))
            .andExpect(jsonPath(
                "$.components.schemas.WaypointPageResponse"
            ).exists())
            .andExpect(jsonPath(
                "$.components.schemas.WaypointSummaryResponse"
            ).exists())
            .andExpect(jsonPath(
                "$.components.schemas.WaypointPageResponse.properties.content"
            ).exists())
            .andExpect(jsonPath(
                "$.components.schemas.WaypointPageResponse.properties.page"
            ).exists())
            .andExpect(jsonPath(
                "$.components.schemas.WaypointPageResponse.properties.size"
            ).exists())
            .andExpect(jsonPath(
                "$.components.schemas.WaypointPageResponse.properties.totalElements"
            ).exists())
            .andExpect(jsonPath(
                "$.components.schemas.WaypointPageResponse.properties.totalPages"
            ).exists())
            .andExpect(jsonPath(
                "$.components.schemas.WaypointPageResponse.properties.first"
            ).exists())
            .andExpect(jsonPath(
                "$.components.schemas.WaypointPageResponse.properties.last"
            ).exists());
    }
}
