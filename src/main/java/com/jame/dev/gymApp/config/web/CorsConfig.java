package com.jame.dev.gymApp.config.web;

import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Component
public class CorsConfig {

   private final List<String> ALLOWED_ORIGINS = List.of("http://locahost:5173");
   private final List<String> ALLOWED_METHODS = List.of(
           "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
   private final List<String> ALLOWED_HEADERS = List.of(
           "Content-Type", "Authorization", "Access-Control-Allow-Headers");

   public CorsConfigurationSource configurationSource() {
      CorsConfiguration cors = new CorsConfiguration();
      cors.setAllowedOrigins(ALLOWED_ORIGINS);
      cors.setAllowedMethods(ALLOWED_METHODS);
      cors.setAllowedHeaders(ALLOWED_HEADERS);
      cors.setAllowCredentials(true);
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", cors);
      return source;
   }
}
