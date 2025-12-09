package com.jame.dev.gymApp.cache.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jame.dev.gymApp.model.dto.out.PageMetaData;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.shared.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import redis.clients.jedis.JedisPooled;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppCacheServiceTest {
   @Mock
   private JedisPooled cacheAppPool;

   @InjectMocks
   private AppCacheServiceImplementation<UserDtoOutput> service;

   private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
   private final UserDtoOutput dto = UserDtoOutput.builder()
           .id(1L)
           .name("dto")
           .email("dto@mail.com")
           .roles(Set.of(Role.ADMIN))
           .build();

   private final Page<UserDtoOutput> page =
           new PageImpl<>(List.of(dto), PageRequest.of(0, 1, Sort.by(Sort.Order.asc("id"))), 1);
   private final PageMetaData metaData =
           new PageMetaData(page.getNumber(),
                   page.getSize(),
                   page.getTotalElements(),
                   page.getTotalPages(),
                   page.getSort().getOrderFor("id").getProperty(), "ASC");
   private final String key = "users:0:1";

   @BeforeEach
   void setUp() {
      service = new AppCacheServiceImplementation<>(UserDtoOutput.class, cacheAppPool);
   }

   private <T> String mapToJson(T t) throws JsonProcessingException {
      return mapper.writeValueAsString(t);
   }

   @Test
   @DisplayName("Should gets the cache list")
   void getCache() throws JsonProcessingException {
      final List<String> lines = List.of(mapToJson(dto));
      final String metadata = mapToJson(metaData);
      when(cacheAppPool.lrange(key, 0, -1))
              .thenReturn(lines);
      when(cacheAppPool.get(key.concat(":meta"))).thenReturn(metadata);

      Optional<Page<UserDtoOutput>> optionalPage = service.getCache(key);

      verify(cacheAppPool).lrange(key, 0, -1);
      verify(cacheAppPool).get(key.concat(":meta"));
      verifyNoMoreInteractions(cacheAppPool);
   }

   @Test
   @DisplayName("Should save the page data")
   void saveCache() throws JsonProcessingException {
      final String json = mapToJson(dto);
      final String metadataSerialized = mapToJson(metaData);

      when(cacheAppPool.del(key)).thenReturn(1L);
      when(cacheAppPool.rpush(key, json)).thenReturn(1L);
      when(cacheAppPool.expire(key, 420)).thenReturn(1L);
      when(cacheAppPool.setex(key.concat(":meta"), 420, metadataSerialized)).thenReturn("OK");

      service.saveCache(key, page);

      verify(cacheAppPool).del(key);
      verify(cacheAppPool, atLeastOnce()).rpush(key, json);
      verify(cacheAppPool).expire(key, 420);
      verify(cacheAppPool).setex(key.concat(":meta"), 420, metadataSerialized);
   }

   @Test
   @DisplayName("Should get object with any id given")
   void getUser() throws JsonProcessingException {
      final String json = mapToJson(dto);
      when(cacheAppPool.exists(eq("users:0:1"))).thenReturn(true);
      when(cacheAppPool.lrange("users:0:1", 0, -1))
              .thenReturn(List.of(json));
      Optional<UserDtoOutput> optionalDto = service.get(key, 1L);

      verify(cacheAppPool).exists(key);
      verify(cacheAppPool).lrange(key, 0, -1);
      verifyNoMoreInteractions(cacheAppPool);
      assertNotSame(Optional.empty(), optionalDto, "Should not be empty");
      assertNotNull(optionalDto.orElse(null), "Should not be null.");
   }

   @Test
   @DisplayName("Should invalidates the given page key")
   void invalidates(){
      when(cacheAppPool.exists(key))
              .thenReturn(true);
      when(cacheAppPool.del(key)).thenReturn(1L);

      service.invalidatePage(key);

      verify(cacheAppPool).exists(key);
      verify(cacheAppPool).del(key);
   }

   @Test
   @DisplayName("Should not invalidate any non-existing key")
   void notValidates(){
      when(cacheAppPool.exists(anyString())).thenReturn(false);

      service.invalidatePage(key);
      ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);

      verify(cacheAppPool).exists(keyCaptor.capture());
      verify(cacheAppPool, never()).del(keyCaptor.capture());
   }

   @Test
   @DisplayName("Should validate if any given key exists.")
   void keyExists(){
      when(cacheAppPool.exists(anyString())).thenReturn(true);

      boolean keyExists = service.keyExists(key);
      ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
      verify(cacheAppPool).exists(keyCaptor.capture());

      assertTrue(keyExists, "key should exists.");
   }

   @Test
   @DisplayName("Should validate if any given key not exists.")
   void keyNotExists(){
      when(cacheAppPool.exists(anyString())).thenReturn(false);

      boolean keyExists = service.keyExists(key);
      ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
      verify(cacheAppPool).exists(keyCaptor.capture());

      assertFalse(keyExists, "key should not exists.");
   }
}