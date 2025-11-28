package com.jame.dev.gymApp.config.web;

import com.jame.dev.gymApp.auth.filters.CustomAuthorizationFilter;
import com.jame.dev.gymApp.auth.handlers.CustomOAuth2AuthenticationHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

   private final CorsConfiguration corsConfiguration;
   private final CustomAuthorizationFilter customAuthorizationFilter;
   private final CustomOAuth2AuthenticationHandler authenticationHandler;

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      return http
              .csrf(AbstractHttpConfigurer::disable)
              .cors(Customizer.withDefaults())
              .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .authorizeHttpRequests(auth -> auth
                      .requestMatchers("/auth/**").permitAll() // own endpoints.
                      .requestMatchers("/oauth2/**").permitAll() //for SPA
                      .requestMatchers("/login/oauth2/**").permitAll() // callback for Google
                      .anyRequest().authenticated()
              )
              //.oauth2Login()
              //.oauth2Client(Customizer.withDefaults())
              .build();
   }

}
