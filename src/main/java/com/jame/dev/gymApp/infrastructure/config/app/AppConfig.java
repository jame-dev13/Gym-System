package com.jame.dev.gymApp.infrastructure.config.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import java.time.Clock;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
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
            if (param == null) {
               sb.append("null");
            } else if (param instanceof Pageable pag) {
               sb.append("page:").append(pag.getPageNumber())
                  .append(":").append(pag.getPageSize())
                  .append(":sort=").append(pag.getSort());
            } else if (param instanceof String s) {
               sb.append("str:").append(s);
            } else {
               sb.append(param.getClass().getSimpleName())
                  .append(":").append(param);
            }
         }
         return sb.toString();
      };
   }
}
