package it.motoroute.common.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    OpenAPI motoRouteOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("MotoRoute API")
                .description(
                    "REST API for creating and managing motorcycle routes."
                )
                .version("0.1.0")
            );
    }
}
