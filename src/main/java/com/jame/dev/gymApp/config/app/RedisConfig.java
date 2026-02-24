package com.jame.dev.gymApp.config.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig {

   private final ObjectMapper mapper;

   @Value("${spring.redis.host}")
   private String host;

   @Value("${spring.redis.port}")
   private int port;

   @Value("${spring.redis.password}")
   private String passwordRedis;

   // 1. Centralize connection logic to avoid duplication
   private RedisStandaloneConfiguration getStandaloneConfig(int database) {
      RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
      config.setPassword(passwordRedis);
      config.setDatabase(database);
      return config;
   }

   @Bean
   public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
      final RedisCacheConfiguration config = RedisCacheConfiguration
              .defaultCacheConfig()
              .prefixCacheNameWith("gym-app:")
              .serializeKeysWith(
                      RedisSerializationContext
                              .SerializationPair
                              .fromSerializer(new StringRedisSerializer())
              )
              .serializeValuesWith(RedisSerializationContext
                      .SerializationPair
                      .fromSerializer(new GenericJackson2JsonRedisSerializer(mapper))
              )
              .entryTtl(Duration.ofMinutes(5));

      return RedisCacheManager.builder(connectionFactory)
              .cacheDefaults(config)
              .build();
   }

   @Bean(name = "tokensRedisTemplate")
   public StringRedisTemplate tokensBlacklister() {
      final JedisConnectionFactory factory = new JedisConnectionFactory(getStandaloneConfig(1));
      factory.afterPropertiesSet();

      return new StringRedisTemplate(factory);
   }
}