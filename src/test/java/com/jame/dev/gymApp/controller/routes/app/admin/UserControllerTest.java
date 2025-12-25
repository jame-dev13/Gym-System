package com.jame.dev.gymApp.controller.routes.app.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jame.dev.gymApp.auth.filters.CustomAuthorizationFilter;
import com.jame.dev.gymApp.cache.service.AppCacheService;
import com.jame.dev.gymApp.controller.advice.ApiErrorResponseFactory;
import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.mapper.UserMapper;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.service.in.UserService;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = CustomAuthorizationFilter.class
                )}
)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private UserController controller;

   @MockitoBean
   private UserService service;

   @MockitoBean
   private UserMapper mapper;

   @MockitoBean
   private AppCacheService<UserDtoOutput> cache;

   @MockitoBean
   private ApiErrorResponseFactory responseFactory;

   private final ObjectMapper objectMapper = new ObjectMapper();
   private final String URI_TEMPLATE = "/admin/users";

   final UserDtoInput input = UserDtoInput.builder()
           .name("input")
           .email("in@mail.com")
           .password("d39f02n4")
           .roles(Set.of(Role.ADMIN))
           .authProvider(AuthProvider.LOCAL)
           .build();
   final UserEntity userEntity = UserEntity.builder()
           .id(1L)
           .name(input.name())
           .email(input.email())
           .password(input.password())
           .roles(Set.of(new RoleEntity(null, Role.ADMIN)))
           .provider(AuthProvider.LOCAL)
           .active(true)
           .build();
   final UserDtoOutput dto = UserDtoOutput.builder()
           .id(userEntity.getId())
           .name(userEntity.getName())
           .email(userEntity.getEmail())
           .roles(userEntity.getRoles().stream().map(RoleEntity::getRole).collect(Collectors.toSet()))
           .build();


   @Test
   @DisplayName("[GET]: Should get page of Users")
   void getPage() throws Exception {
      final Pageable pageable = PageRequest.of(0, 1);

      final Page<@NonNull UserEntity> entityPage = new PageImpl<>(List.of(userEntity), pageable, pageable.getPageSize());
      when(cache.getCache(anyString())).thenReturn(Optional.empty());
      when(service.getPage(any(Pageable.class))).thenReturn(entityPage);
      when(mapper.toDto(any(UserEntity.class))).thenReturn(dto);

      final Page<@NonNull UserDtoOutput> pageDto = new PageImpl<>(List.of(dto), pageable, 1);
      final String jsonExpected = objectMapper.writeValueAsString(pageDto);
      mockMvc.perform(get(URI_TEMPLATE)
                      .param("page", "0")
                      .param("size", "1")
                      .accept(MediaType.APPLICATION_JSON.toString()))
              .andExpectAll(status().isOk(), content().json(jsonExpected));

      final ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
      final ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
      final ArgumentCaptor<UserEntity> entityCaptor = ArgumentCaptor.forClass(UserEntity.class);

      verify(cache).getCache(keyCaptor.capture());
      verify(service).getPage(pageableCaptor.capture());
      verify(mapper).toDto(entityCaptor.capture());
      verifyNoMoreInteractions(service, mapper);
   }

   @Test
   @DisplayName("[GET]: Should Get page from cache")
   void getFromCache() throws Exception {
      final String key = "users:0:1";
      Pageable pageable = PageRequest.of(0, 1);
      Page<@NonNull UserDtoOutput> pageDto = new PageImpl<>(List.of(dto), pageable, 1);
      when(cache.getCache(key)).thenReturn(Optional.of(pageDto));

      String jsonExpected = objectMapper.writeValueAsString(pageDto);
      mockMvc.perform(get(URI_TEMPLATE)
                      .param("page", "0")
                      .param("size", "1")
                      .accept(MediaType.APPLICATION_JSON.toString()))
              .andExpectAll(status().isOk(), content().json(jsonExpected));
      verify(cache).getCache(key);
      verifyNoMoreInteractions(cache);
   }

   @Test
   @DisplayName("[GET]: Should get the user specified by id")
   void getOneUser() throws Exception {
      final long id = 1L;
      final String URI = URI_TEMPLATE + '/' + id;
      when(service.getById(anyLong())).thenReturn(Optional.of(userEntity));
      when(mapper.toDto(userEntity)).thenReturn(dto);

      final String jsonExpected = objectMapper.writeValueAsString(dto);
      mockMvc.perform(get(URI)
                      .param("id", "1")
                      .accept(org.springframework.http.MediaType.APPLICATION_JSON))
              .andExpectAll(status().isOk(), content().json(jsonExpected));

      ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
      verify(service).getById(idCaptor.capture());
      verify(mapper).toDto(userEntity);
      verifyNoMoreInteractions(cache, service, mapper);
   }

   @Test
   @DisplayName("[POST]: Should do post to an user")
   void postUser() throws Exception {

      when(service.save(any(UserDtoInput.class))).thenReturn(userEntity);
      when(mapper.toDto(any(UserEntity.class))).thenReturn(dto);

      final String jsonInput = objectMapper.writeValueAsString(input);
      final String jsonOutput = objectMapper.writeValueAsString(dto);

      mockMvc.perform(post(URI_TEMPLATE)
                      .contentType(MediaType.APPLICATION_JSON.toString())
                      .accept(MediaType.APPLICATION_JSON.toString())
                      .content(jsonInput))
              .andExpect(status().isCreated())
              .andExpect(content().json(jsonOutput));

      ArgumentCaptor<UserDtoInput> inputCaptor = ArgumentCaptor.forClass(UserDtoInput.class);
      ArgumentCaptor<UserEntity> entityCaptor = ArgumentCaptor.forClass(UserEntity.class);

      verify(service).save(inputCaptor.capture());
      verify(mapper).toDto(entityCaptor.capture());
      verifyNoMoreInteractions(service, mapper);

      final UserDtoInput userDtoInput = inputCaptor.getValue();
      assertNotNull(inputCaptor, "DtoInput should not be null.");
      assertEquals(input, userDtoInput, "DtoInputs Should be the same ones.");

      final UserEntity userEntity = entityCaptor.getValue();
      assertNotNull(entityCaptor, "UserEntity should not be null.");
      assertSame(userEntity, this.userEntity, "UserEntities should be the same one.");
      assertEquals(userDtoInput.name(), userEntity.getName(), "Names should be the same.");
      assertEquals(userDtoInput.email(), userEntity.getEmail(), "Emails should be the same.");
   }

   @Test
   @DisplayName("[PUT]: Should do put to an user object.")
   void putUser() throws Exception {
      final long id = 1L;
      final UserDtoInput input = UserDtoInput.builder()
              .name("input1")
              .email("in1@mail.com")
              .password("d39f02n4")
              .roles(Set.of(Role.ADMIN))
              .authProvider(AuthProvider.LOCAL)
              .build();
      final UserEntity userEntity = UserEntity.builder()
              .id(1L)
              .name(input.name())
              .email(input.email())
              .password(input.password())
              .roles(Set.of(new RoleEntity(null, Role.ADMIN)))
              .provider(AuthProvider.LOCAL)
              .active(true)
              .build();

      final UserDtoOutput dto = UserDtoOutput.builder()
              .id(userEntity.getId())
              .name(userEntity.getName())
              .email(userEntity.getEmail())
              .roles(userEntity.getRoles().stream().map(RoleEntity::getRole).collect(Collectors.toSet()))
              .build();

      when(service.update(id, input)).thenReturn(userEntity);
      when(mapper.toDto(userEntity)).thenReturn(dto);

      final String jsonInput = objectMapper.writeValueAsString(input);
      final String jsonOutput = objectMapper.writeValueAsString(dto);
      mockMvc.perform(put(URI_TEMPLATE + "/" + id)
                      .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                      .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                      .param("id", "1")
                      .content(jsonInput))
              .andExpectAll(status().isOk(), content().json(jsonOutput));

      ArgumentCaptor<Long> captorId = ArgumentCaptor.forClass(Long.class);
      ArgumentCaptor<UserDtoInput> captorInput = ArgumentCaptor.forClass(UserDtoInput.class);
      ArgumentCaptor<UserEntity> captorEntity = ArgumentCaptor.forClass(UserEntity.class);

      verify(service).update(captorId.capture(), captorInput.capture());
      verify(mapper).toDto(captorEntity.capture());
      verifyNoMoreInteractions(service, mapper);

      final Long idCaptor = captorId.getValue();
      assertNotNull(idCaptor, "Id should not be null.");
      assertEquals(id, idCaptor, "Id should be equals.");
      assertSame(1L, idCaptor, "Id should be '1L'");

      final UserDtoInput inputCaptor = captorInput.getValue();
      assertNotNull(inputCaptor, "DtoInput should not be null.");
      assertEquals(input, inputCaptor, "DtoInputs Should be the same ones.");

      final UserEntity entityCaptor = captorEntity.getValue();
      assertNotNull(entityCaptor, "UserEntity should not be null.");
      assertSame(entityCaptor, userEntity, "UserEntities should be the same one.");
      assertEquals(inputCaptor.name(), entityCaptor.getName(), "Names should be the same.");
      assertEquals(inputCaptor.email(), entityCaptor.getEmail(), "Emails should be the same.");
   }

   @Test
   @DisplayName("[DELETE]: Should do delete to the user resource.")
   void deleteUser() throws Exception {
      final long id = 1L;
      mockMvc.perform(delete(URI_TEMPLATE + "/" + id)
                      .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                      .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                      .param("id", "1"))
              .andExpect(status().isNoContent());
      verify(service).softDelete(id);
      verifyNoMoreInteractions(service, mapper);
   }
}
