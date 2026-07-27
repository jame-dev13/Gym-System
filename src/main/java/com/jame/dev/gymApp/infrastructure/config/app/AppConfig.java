package com.jame.dev.gymApp.infrastructure.config.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jame.dev.gymApp.application.model.LockProperties;
import com.jame.dev.gymApp.features.backup.domain.model.BackupMapping;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import java.time.Clock;
import java.util.Optional;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@EnableJpaAuditing
@EnableMongoAuditing
@EnableConfigurationProperties(value = {BackupMapping.class, LockProperties.class})
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
      return Clock.systemUTC();
   }

   @Bean("pageKeyGenerator")
   public KeyGenerator pageKeyGenerator() {
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
               case null -> {}
               default -> sb.append(":").append(param);
            }
         }
         return sb.toString();
      };
   }

   @Bean("appKeyGenerator")
   public KeyGenerator appKeyGenerator() {
      return (target, method, params) -> {
         final StringBuilder sb = new StringBuilder();
         sb.append(target.getClass().getSimpleName())
            .append("::").append(method.getName());

         Optional.of(params)
            .ifPresent(p -> {
               for (Object parm : p) {
                  sb.append(":").append(parm);
               }
            });

         return sb.toString();
      };
   }
}
