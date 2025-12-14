package com.jame.dev.gymApp.config.web;

import com.jame.dev.gymApp.auth.filters.CustomAuthorizationFilter;
import com.jame.dev.gymApp.auth.handlers.CustomAccessDeniedHandler;
import com.jame.dev.gymApp.auth.handlers.CustomAuthenticationEntryPointHandler;
import com.jame.dev.gymApp.auth.handlers.CustomLogoutHandler;
import com.jame.dev.gymApp.oauth2.handlers.CustomOAuth2AuthenticationHandler;
import com.jame.dev.gymApp.oauth2.handlers.CustomOAuth2FailureHandler;
import com.jame.dev.gymApp.oauth2.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

   private final CorsConfig corsConfig;
   private final CustomAuthorizationFilter customAuthorizationFilter;
   private final CustomLogoutHandler customLogoutHandler;
   private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;
   private final CustomAccessDeniedHandler customAccessDeniedHandler;
   private final CustomOAuth2UserService auth2UserService;
   private final CustomOAuth2AuthenticationHandler oauth2AuthenticationHandler;
   private final CustomOAuth2FailureHandler oAuth2FailureHandler;

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      return http
              .csrf(AbstractHttpConfigurer::disable)
              .cors(cors -> cors.configurationSource(corsConfig.configurationSource()))
              .authorizeHttpRequests(auth -> auth
                      .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
                      .requestMatchers(HttpMethod.PATCH, "/auth/verify/**").permitAll()
                      .requestMatchers("/oauth2/**", "/login/oauth2/**", "/oauth2/authorization/**").permitAll()
                      .requestMatchers("/error").permitAll()
                      .requestMatchers("/favicon.ico").permitAll()
                      .anyRequest().authenticated()
              )
              .sessionManagement(session -> session
                      .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
              .exceptionHandling(ex -> ex
                      .authenticationEntryPoint(customAuthenticationEntryPointHandler)
                      .accessDeniedHandler(customAccessDeniedHandler)
              )
              .logout(logout -> logout
                      .logoutUrl("/auth/logout")
                      .addLogoutHandler(customLogoutHandler)
                      .logoutSuccessHandler((request,
                                             response, auth) -> {
                         response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                         SecurityContextHolder.clearContext();
                      })
              )
              .oauth2Login(oauth2 -> oauth2
                      .authorizationEndpoint(a -> a
                              .baseUri("/oauth2/authorization"))
                      .redirectionEndpoint(r -> r
                              .baseUri("/login/oauth2/code/*"))
                      .userInfoEndpoint(us -> us
                              .userService(auth2UserService))
                      .successHandler(oauth2AuthenticationHandler)
                      .failureHandler(oAuth2FailureHandler)
              )
              .addFilterBefore(customAuthorizationFilter,
                      UsernamePasswordAuthenticationFilter.class)
              .build();
   }

}
