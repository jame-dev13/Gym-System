package com.jame.dev.gymApp.cache.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jame.dev.gymApp.exception.IndexNotFoundException;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import redis.clients.jedis.JedisPooled;

import java.util.List;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppCacheServiceTest {
   @Mock
   private JedisPooled cacheAppPool;
   @InjectMocks
   private AppCacheServiceImplementation<UserDtoOutput> service;

   private UserDtoOutput userDto;
   private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

   @BeforeEach
   void setUp() {
      service = new AppCacheServiceImplementation<>(UserDtoOutput.class, cacheAppPool);
      this.userDto = UserDtoOutput.builder()
              .name("someone")
              .email("someone@mail.com")
              .build();
   }

   @Test
   @DisplayName("Get cache list.")
   void getCache() throws JsonProcessingException {
      String expected = mapper.writeValueAsString(userDto);
      when(cacheAppPool.lrange(eq("users"), eq(0L), eq(-1L)))
              .thenReturn(List.of(expected));
      List<UserDtoOutput> list = Assertions
              .assertDoesNotThrow(() -> service.getCache("users"), "Should not throw Exceptions.");
      Assertions.assertAll("List not null, not empty, and contains the DTO object",
              () -> Assertions.assertNotNull(list, "Should not be null."),
              () -> Assertions.assertFalse(list.isEmpty(), "Should not be empty."),
              () -> Assertions.assertTrue(list.contains(userDto), "Should contain the DTO object.")
      );

      verify(cacheAppPool).lrange(eq("users"), eq(0L), eq(-1L));
   }

   @Test
   @DisplayName("Save List in cache.")
   void saveCache() throws JsonProcessingException{
      List<UserDtoOutput> list = List.of(
              new UserDtoOutput( "A", "B"),
              new UserDtoOutput( "C", "D")
      );

      when(cacheAppPool.del(eq("users"))).thenReturn(1L);
      when(cacheAppPool.rpush(eq("users"), anyString())).thenReturn(1L);
      when(cacheAppPool.expire(eq("users"), eq(420L))).thenReturn(1L);

      service.saveCache("users", list);

      ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
      verify(cacheAppPool).del(eq("users"));
      verify(cacheAppPool, times(2)).rpush(eq("users"), captor.capture());
      verify(cacheAppPool).expire(eq("users"), eq(420L));

      List<String> jsonValues = captor.getAllValues();

      Assertions.assertEquals(jsonValues.getFirst(),
              mapper.writeValueAsString(list.getFirst()), "Should be equals.");
      Assertions.assertEquals(jsonValues.getLast(),
              mapper.writeValueAsString(list.getLast()), "Should be equals");
   }

   @Test
   @DisplayName("Add item to an element into the cache collection.")
   void addToCache() throws JsonProcessingException {
      String value = mapper.writeValueAsString(userDto);
      when(cacheAppPool.lrange(eq("users"), eq(0L), eq((-1L))))
              .thenReturn(List.of());
      when(cacheAppPool.rpush(eq("users"), eq(value))).thenReturn(1L);

      service.addToCache("users", userDto);

      verify(cacheAppPool).rpush(eq("users"), eq(value));
   }

   @Test
   @DisplayName("Not addition when the item is already in the cache collection.")
   void failAddCacheWhenExistTheElement() throws JsonProcessingException {
      String value = mapper.writeValueAsString(userDto);
      when(cacheAppPool.lrange(eq("users"), eq(0L), eq((-1L))))
              .thenReturn(List.of(value));
      service.addToCache("users", userDto);

      verify(cacheAppPool, never()).rpush(eq("users"), eq(value));
   }

   @Test
   @DisplayName("Update item in cache collection.")
   void updateItemInCache() throws JsonProcessingException {
      String email = userDto.email();
      UserDtoOutput updateDto = new UserDtoOutput("updateName", "update@mail.com");
      Predicate<UserDtoOutput> filter = dto -> dto.email().equals(email);
      String existingJson = mapper.writeValueAsString(userDto);
      when(cacheAppPool.lrange(eq("users"), eq(0L), eq(-1L)))
              .thenReturn(List.of(existingJson));
      when(cacheAppPool.expireTime(eq("users"))).thenReturn(200L);

      service.updateItemInCache("users", filter, updateDto);
      String updatedJson = mapper.writeValueAsString(updateDto);

      verify(cacheAppPool).lset(eq("users"), eq(0L), eq(updatedJson));
      verify(cacheAppPool).expire(eq("users"), eq(200L));
   }

   @Test
   @DisplayName("Index == -1")
   void indexNotFound(){
      Predicate<UserDtoOutput> filter = u -> u.email().equals("unkwon@mail.com");
      when(cacheAppPool.lrange(eq("users"), eq(0L), eq(-1L)))
              .thenReturn(List.of());
      Assertions.assertThrows(IndexNotFoundException.class,
              () -> service.updateItemInCache("users", filter, this.userDto), "Should throws an Exception");
      verify(cacheAppPool).lrange(eq("users"), eq(0L), eq(-1L));
      verify(cacheAppPool, never()).lset(eq("users"), anyLong(), anyString());
      verify(cacheAppPool, never()).expireTime(eq("users"));
      verify(cacheAppPool, never()).expire(eq("users"), anyLong());
   }

   @Test
   @DisplayName("TTL sets to 420 on 0")
   void setTtl() throws JsonProcessingException {
      UserDtoOutput existing = new UserDtoOutput("john", "john@mail.com");
      UserDtoOutput updated  = new UserDtoOutput("john", "new@mail.com");

      String existingJson = mapper.writeValueAsString(existing);
      String updatedJson  = mapper.writeValueAsString(updated);
      when(cacheAppPool.lrange(eq("users"), eq(0L), eq(-1L)))
              .thenReturn(List.of(existingJson));
      when(cacheAppPool.expireTime(eq("users"))).thenReturn(0L);

      Predicate<UserDtoOutput> filter = u -> u.name().equals("john");
      service.updateItemInCache("users", filter, updated);

      verify(cacheAppPool).lrange(eq("users"), eq(0L), eq(-1L));
      verify(cacheAppPool).lset("users", 0, updatedJson);
      verify(cacheAppPool).expireTime("users");
      verify(cacheAppPool).expire("users", 420L);
   }
}