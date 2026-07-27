package it.motoroute.integration.openapi;

import it.motoroute.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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
}
