package com.pharmaorder.apigateway.config;

import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public CommandLineRunner openApiGroups(RouteDefinitionLocator locator, SwaggerUiConfigParameters swaggerUiConfigParameters) {
        return args -> locator.getRouteDefinitions().collectList().block().stream()
                .map(routeDefinition -> routeDefinition.getId())
                .filter(id -> id.matches(".*-service$"))
                .forEach(id -> swaggerUiConfigParameters.addGroup(id));
    }
}
