package com.jame.dev.gymApp.config.app;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jame.dev.gymApp.model.mixIn.DefaultMixInDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AssignableTypeFilter;
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

@Slf4j
@Configuration
@EnableCaching
public class RedisConfig {

   @Value("${spring.data.redis.host}")
   private String host;

   @Value("${spring.data.redis.port}")
   private int port;

   @Value("${spring.data.redis.password}")
   private String passwordRedis;

   // 1. Centralize connection logic to avoid duplication
   private RedisStandaloneConfiguration getStandaloneConfig(int database) {
      RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
      config.setPassword(passwordRedis);
      config.setDatabase(database);
      return config;
   }

   @Bean
   public PolymorphicTypeValidator getPolymorphicTypeValidator() {
      return BasicPolymorphicTypeValidator.builder()
              .allowIfSubType("com.jame.dev.gymApp")
              .allowIfSubType("org.springframework.data.domain")
              .allowIfBaseType(java.util.Collection.class)
              .allowIfBaseType(java.util.Map.class)
              .allowIfBaseType(java.time.temporal.Temporal.class)
              .allowIfBaseType(java.lang.Number.class)
              .allowIfBaseType(Object.class)
              .build();
   }

   @Bean("redisCacheManager")
   public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
      final PolymorphicTypeValidator ptv = getPolymorphicTypeValidator();
      final ObjectMapper redisMapper = new ObjectMapper();
      registerMixIns(redisMapper, "com.jame.dev.gymApp");

      redisMapper.registerModule(new JavaTimeModule())
              .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
              .activateDefaultTyping(
                      ptv,
                      ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE,
                      JsonTypeInfo.As.PROPERTY
              );

      RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
              .prefixCacheNameWith("gym-app:")
              .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
              .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(redisMapper)))
              .entryTtl(Duration.ofMinutes(10));

      return RedisCacheManager.builder(connectionFactory).cacheDefaults(config).build();
   }

   /**
    * Scans the given package and searches for dto package and sub packages inside and adds the mixIn there
    * Requires a scan library or ClassPathScanningCandidateComponentProvider from Spring.
    */
   private void registerMixIns(ObjectMapper mapper, String basePackage) {
      ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
      scanner.addIncludeFilter(new AssignableTypeFilter(Object.class));

      scanner.findCandidateComponents(basePackage).forEach(beanDefinition -> {
         try {
            Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
            if (clazz.isRecord() || clazz.getName().contains(".dto.")) {
               mapper.addMixIn(clazz, DefaultMixInDto.class);
            }
         } catch (ClassNotFoundException e) {
            log.error("{}", e.getMessage());
         }
      });
   }

   @Bean(name = "tokensRedisTemplate")
   public StringRedisTemplate tokensBlacklister() {
      final JedisConnectionFactory factory = new JedisConnectionFactory(getStandaloneConfig(1));
      factory.afterPropertiesSet();

      return new StringRedisTemplate(factory);
   }

   @Bean("fixedWindowTemplate")
   public StringRedisTemplate fixedWindowTemplate() {
      final JedisConnectionFactory factory = new JedisConnectionFactory(getStandaloneConfig(2));
      factory.afterPropertiesSet();

      return new StringRedisTemplate(factory);
   }

   @Bean("blockingListTemplate")
   public StringRedisTemplate blockingList() {
      final JedisConnectionFactory factory = new JedisConnectionFactory(getStandaloneConfig(3));
      factory.afterPropertiesSet();

      return new StringRedisTemplate(factory);
   }
}