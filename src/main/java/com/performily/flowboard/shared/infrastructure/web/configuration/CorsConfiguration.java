package com.performily.flowboard.shared.infrastructure.web.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cors Configuration
 * @summary
 * Allows the Angular Web Application to consume the RESTful API from another origin.
 *
 * The allowed origins are read from the {@code cors.allowed-origins} property.
 *
 * @since 1.0.0
 */
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {
    private final String[] allowedOrigins;

    /**
     * Creates the configuration with the allowed origins.
     *
     * @param allowedOrigins comma separated list of allowed origins
     */
    public CorsConfiguration(@Value("${cors.allowed-origins}") String[] allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Location")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
