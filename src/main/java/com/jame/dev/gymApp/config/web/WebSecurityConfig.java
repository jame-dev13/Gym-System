package com.jame.dev.gymApp.config.web;

import com.jame.dev.gymApp.auth.filters.CustomAuthorizationFilter;
import com.jame.dev.gymApp.auth.handlers.CustomLogoutHandler;
import com.jame.dev.gymApp.auth.handlers.CustomOAuth2AuthenticationHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

   private final CorsConfiguration corsConfiguration;
   private final CustomAuthorizationFilter customAuthorizationFilter;
   private final CustomOAuth2AuthenticationHandler authenticationHandler;
   private final CustomLogoutHandler customLogoutHandler;

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
              .logout(logout -> {
                 logout.logoutUrl("/auth/logout");
                 logout.addLogoutHandler(customLogoutHandler);
                 logout.logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    SecurityContextHolder.clearContext();
                 });
              })
              .addFilterBefore(customAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
              .build();
   }

}
