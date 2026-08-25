package com.jame.dev.gymApp.infrastructure.config.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jame.dev.gymApp.application.model.LockProperties;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.infrastructure.auth.AuthenticationUserResolver;
import com.jame.dev.gymApp.features.backup.domain.model.BackupMapping;
import com.jame.dev.gymApp.infrastructure.properties.SchedulerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.Authentication;

import java.time.Clock;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@EnableJpaAuditing
@EnableConfigurationProperties(value = {BackupMapping.class, LockProperties.class, SchedulerProperties.class})
@EnableScheduling
public class AppConfig {

   @Bean(name = "mapper")
   @Primary
   public ObjectMapper mapper() {
      return new ObjectMapper()
         .registerModule(new JavaTimeModule())
         .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
   }

   @Bean(name = "clock")
   public Clock clock() {
      return Clock.systemUTC().withZone(ZoneId.of("America/Mexico_City"));
   }

   @Bean("pageKeyGenerator")
   public KeyGenerator pageKeyGenerator(final AuthenticationUserResolver authenticationUserResolver) {
      return (ignoredTarget, ignoredMethod, params) -> {
         final StringBuilder sb = new StringBuilder();
         for (int i = 0; i < params.length; i++) {
            if (i > 0) sb.append("|");
            final Object param = params[i];
            switch (param) {
               case Pageable pag -> sb.append("page:").append(pag.getPageNumber())
                  .append(":").append(pag.getPageSize())
                  .append(":sort=").append(pag.getSort());
               case String s -> sb.append("str:").append(s);
               case Authentication auth -> sb
                  .append("auth-current-id:")
                  .append(authenticationUserResolver.resolveUserId(auth));
               case null -> {
               }
               default -> sb.append(":").append(param);
            }
         }
         return sb.toString();
      };
   }

   @Bean("appKeyGenerator")
   public KeyGenerator appKeyGenerator(AuthenticationUserResolver authenticationUserResolver) {
      return (target, method, params) -> {
         final StringBuilder sb = new StringBuilder();
         sb.append(target.getClass().getSimpleName())
            .append("::").append(method.getName());

         Optional.of(params)
            .ifPresent(p -> {
               for (Object param : p) {
                  if (param instanceof Authentication auth) {
                     sb
                        .append(":current:")
                        .append(authenticationUserResolver.resolveUserId(auth));
                     return;
                  }
                  sb.append(":").append(param);
               }
            });

         return sb.toString();
      };
   }

   @Bean("authCurrentKeyGen")
   public KeyGenerator authCurrentKeyGenerator(final AuthenticationUserResolver authenticationUserResolver) {
      return (targetIgnored, methodIgnored, params) -> {

         final Authentication authentication = Arrays.stream(params)
            .filter(Authentication.class::isInstance)
            .map(Authentication.class::cast)
            .findFirst()
            .orElseThrow(() ->
               new IllegalArgumentException(
                  "Authentication parameter not found."
               )
            );

         final Long userId = authenticationUserResolver.resolveUserId(authentication);

         return "auth-current:" + userId;
      };
   }

   @Bean("authPrincipalCurrentKeyGen")
   public KeyGenerator authPrincipalCurrentKeyGenerator() {
      return (targetIgnored, methodIgnored, params) -> {

         final AuthPrincipal principal = Arrays.stream(params)
            .filter(AuthPrincipal.class::isInstance)
            .map(AuthPrincipal.class::cast)
            .findFirst()
            .orElseThrow(
               () -> new IllegalArgumentException("Auth Principal param not found.")
            );

         return ":auth-current:" + principal.id();
      };
   }
}
