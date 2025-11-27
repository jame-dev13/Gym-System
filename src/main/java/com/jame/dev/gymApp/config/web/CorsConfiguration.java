package com.jame.dev.gymApp.config.web;

import org.springframework.stereotype.Component;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Component
public class CorsConfiguration {

   private final List<String> ALLOWED_ORIGINS = List.of("http://locahost:5173");
   private final List<String> ALLOWED_METHODS = List.of(
           "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
   private final List<String> ALLOWED_HEADERS = List.of(
           "Content-Type", "Authorization", "Access-Control-Allow-Headers");

   public void configurationSource(){
      org.springframework.web.cors.CorsConfiguration cors = new org.springframework.web.cors.CorsConfiguration();
      cors.setAllowedOrigins(ALLOWED_ORIGINS);
      cors.setAllowedMethods(ALLOWED_METHODS);
      cors.setAllowedHeaders(ALLOWED_HEADERS);
      cors.setAllowCredentials(true);
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", cors);
   }
}
