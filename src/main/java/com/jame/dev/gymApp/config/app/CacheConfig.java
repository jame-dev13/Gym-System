package com.jame.dev.gymApp.config.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jame.dev.gymApp.cache.service.AppCacheService;
import com.jame.dev.gymApp.cache.service.AppCacheServiceImplementation;
import com.jame.dev.gymApp.model.dto.out.CustomerDtoOutput;
import com.jame.dev.gymApp.model.dto.out.PricingDtoOutput;
import com.jame.dev.gymApp.model.dto.out.SubscriptionDtoOutput;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {

   private final JedisPooled cacheAppPool;
   private final ObjectMapper mapper;

   @Bean
   public AppCacheService<UserDtoOutput> cacheUsers(){
      return new AppCacheServiceImplementation<>(UserDtoOutput.class, cacheAppPool, mapper);
   }

   @Bean
   public AppCacheService<CustomerDtoOutput> cacheCustomers(){
      return new AppCacheServiceImplementation<>(CustomerDtoOutput.class, cacheAppPool, mapper);
   }

   @Bean
   public AppCacheService<SubscriptionDtoOutput> cacheSubscriptions(){
      return new AppCacheServiceImplementation<>(SubscriptionDtoOutput.class, cacheAppPool, mapper);
   }

   @Bean
   public AppCacheService<PricingDtoOutput> cachePrices(){
      return new AppCacheServiceImplementation<>(PricingDtoOutput.class, cacheAppPool, mapper);
   }
}
