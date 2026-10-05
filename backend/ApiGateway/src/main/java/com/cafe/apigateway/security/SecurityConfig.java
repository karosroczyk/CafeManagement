package com.cafe.apigateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtAuthWebFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                "/auth/**",
                                "/v2/api-docs",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html"
                        )
                        .permitAll()

                        // Example role checks
                        // .pathMatchers("/api/menuitems/**")
                        // .hasAnyRole("CLIENT", "EMPLOYEE")

                        .anyExchange()
                        .authenticated()
                )

                .addFilterAt(
                        jwtAuthWebFilter,
                        SecurityWebFiltersOrder.AUTHENTICATION
                )

                .build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return exchange -> {
            var config = new CorsConfiguration();

            config.setAllowedOrigins(List.of(
                    "http://localhost:4200",
                    "http://localhost:8080"
            ));

            config.setAllowedMethods(List.of(
                    "GET",
                    "POST",
                    "PUT",
                    "DELETE",
                    "OPTIONS"
            ));

            config.setAllowedHeaders(List.of(
                    "Authorization",
                    "Content-Type"
            ));

            config.setAllowCredentials(true);

            return config;
        };
    }
}