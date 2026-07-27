package it.motoroute.route.api;

import it.motoroute.common.api.GlobalExceptionHandler;
import it.motoroute.route.application.RouteService;
import it.motoroute.route.domain.Difficulty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RouteController.class)
@Import(GlobalExceptionHandler.class)
class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RouteService routeService;

    @Test
    void shouldReturn201AndLocationWhenRequestIsValid() throws Exception {
        UUID routeId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        RouteResponse response = new RouteResponse(
            routeId,
            "Passo dello Stelvio",
            "Percorso panoramico",
            "Bormio",
            "Prato allo Stelvio",
            new BigDecimal("47.50"),
            Difficulty.HARD,
            now,
            now
        );

        when(routeService.createRoute(any(CreateRouteRequest.class)))
            .thenReturn(response);

        mockMvc.perform(post("/api/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Passo dello Stelvio",
                      "description": "Percorso panoramico",
                      "startLocation": "Bormio",
                      "endLocation": "Prato allo Stelvio",
                      "distanceKm": 47.50,
                      "difficulty": "HARD"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(header().string(
                "Location",
                "/api/routes/" + routeId
            ))
            .andExpect(jsonPath("$.id").value(routeId.toString()))
            .andExpect(jsonPath("$.name").value("Passo dello Stelvio"))
            .andExpect(jsonPath("$.description").value("Percorso panoramico"))
            .andExpect(jsonPath("$.startLocation").value("Bormio"))
            .andExpect(jsonPath("$.endLocation").value("Prato allo Stelvio"))
            .andExpect(jsonPath("$.distanceKm").value(47.50))
            .andExpect(jsonPath("$.difficulty").value("HARD"))
            .andExpect(jsonPath("$.createdAt").value(now.toString()))
            .andExpect(jsonPath("$.updatedAt").value(now.toString()));

        verify(routeService).createRoute(any(CreateRouteRequest.class));
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "   ",
                      "description": "Percorso panoramico",
                      "startLocation": "Bormio",
                      "endLocation": "Prato allo Stelvio",
                      "distanceKm": 47.50,
                      "difficulty": "HARD"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message")
                .value("One or more fields are invalid"))
            .andExpect(jsonPath("$.path").value("/api/routes"))
            .andExpect(jsonPath("$.fieldErrors.name").exists());

        verifyNoInteractions(routeService);
    }

    @Test
    void shouldReturn400WhenDistanceIsNegative() throws Exception {
        mockMvc.perform(post("/api/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Passo dello Stelvio",
                      "description": "Percorso panoramico",
                      "startLocation": "Bormio",
                      "endLocation": "Prato allo Stelvio",
                      "distanceKm": -1,
                      "difficulty": "HARD"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.distanceKm").exists());

        verifyNoInteractions(routeService);
    }

    @Test
    void shouldReturn400WhenDifficultyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Passo dello Stelvio",
                      "description": "Percorso panoramico",
                      "startLocation": "Bormio",
                      "endLocation": "Prato allo Stelvio",
                      "distanceKm": 47.50,
                      "difficulty": "EXTREME"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message")
                .value("Malformed or invalid JSON request"));

        verifyNoInteractions(routeService);
    }

    @Test
    void shouldReturn400WhenJsonIsMalformed() throws Exception {
        mockMvc.perform(post("/api/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Passo dello Stelvio",
                      "distanceKm":
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message")
                .value("Malformed or invalid JSON request"));

        verifyNoInteractions(routeService);
    }
}
