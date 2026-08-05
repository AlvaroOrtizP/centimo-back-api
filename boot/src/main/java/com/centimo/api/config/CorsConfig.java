package com.centimo.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Aplica CORS a todas las rutas de la API
                        .allowedOrigins(
                                "http://localhost:4200", // Puerto por defecto del Angular CLI en desarrollo
                                "http://localhost:80",   // Opcional: si ejecutas Angular en Docker/Nginx local
                                "https://centimo-web.onrender.com" // Web desplegada en Render
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // Métodos HTTP permitidos
                        .allowedHeaders("*") // Permite cualquier cabecera (Content-Type, Authorization, etc.)
                        .allowCredentials(true) // Permite el envío de cookies o cabeceras de autenticación si las usas
                        .maxAge(3600); // Mantiene en caché el resultado del Preflight (OPTIONS) durante 1 hora
            }
        };
    }
}
