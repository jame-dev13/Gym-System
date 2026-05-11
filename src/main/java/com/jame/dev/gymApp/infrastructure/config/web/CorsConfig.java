package com.jame.dev.gymApp.infrastructure.config.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Component
public class CorsConfig {

   @Value("${app.cors.allowed-origins:}")
   private List<String> allowedOrigins;

   private final List<String> ALLOWED_METHODS = List.of(
      "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

   private final List<String> ALLOWED_HEADERS = List.of(
      "Content-Type", "Authorization", "X-Requested-With", "Accept", "Origin");

   @Bean
   @Profile("dev")
   public CorsConfigurationSource configurationSourceDev() {
      return createSource(allowedOrigins);
   }

   @Bean
   @Profile("prod")
   public CorsConfigurationSource configurationSourceProd() {
      return createSource(allowedOrigins);
   }

   private CorsConfigurationSource createSource(final List<String> origins) {
      final CorsConfiguration cors = new CorsConfiguration();
      cors.setAllowedOrigins(origins);
      cors.setAllowedMethods(ALLOWED_METHODS);
      cors.setAllowedHeaders(ALLOWED_HEADERS);
      cors.setAllowCredentials(true);
      cors.setMaxAge(3600L);

      final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", cors);
      return source;
   }
}
