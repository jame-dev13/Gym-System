package com.jame.dev.gymApp.config.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPooled;

@Configuration
public class AppConfig {

   @Value("${redis.host}")
   private String host;

   @Value("${redis.port}")
   private int port;

   @Value("${redis.password.dev}")
   private String passwordRedis;

   @Bean(name = "tokensPool")
   public JedisPooled tokensPool(){
      return new JedisPooled(new HostAndPort(host, port),
              DefaultJedisClientConfig.builder()
                      .password(passwordRedis)
                      .database(0)
                      .build());
   }

   @Bean(name = "cacheAppPool")
   public JedisPooled cacheAppPool(){
      return new JedisPooled(new HostAndPort(host, port),
              DefaultJedisClientConfig.builder()
                      .password(passwordRedis)
                      .database(1)
                      .build());
   }

   @Bean(name = "mapper")
   public ObjectMapper mapper(){
      ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());
      mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      return mapper;
   }
}
