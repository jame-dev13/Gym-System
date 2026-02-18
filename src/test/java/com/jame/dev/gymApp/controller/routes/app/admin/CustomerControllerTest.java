package com.jame.dev.gymApp.controller.routes.app.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jame.dev.gymApp.auth.filters.CustomAuthorizationFilter;
import com.jame.dev.gymApp.cache.service.AppCacheService;
import com.jame.dev.gymApp.controller.advice.ApiErrorResponseFactory;
import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.model.dto.out.CustomerDtoOutput;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.service.in.CustomerService;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = CustomerController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = CustomAuthorizationFilter.class
                )}
)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private CustomerController controller;

   @MockitoBean
   private CustomerService customerService;

   @MockitoBean
   private BaseMapper<CustomerEntity, CustomerDtoOutput> mapper;

   @MockitoBean
   private AppCacheService<CustomerDtoOutput> cache;

   @MockitoBean
   private ApiErrorResponseFactory responseFactory;

   private final ObjectMapper objectMapper = new ObjectMapper();

   private final String URI_TEMPLATE = "/admin/customers";
   private final CustomerEntity customer = CustomerEntity.builder()
           .user(new UserEntity())
           .phoneContact("2244234")
           .build();

   @Test
   @DisplayName("Should get the page")
   void getPage() throws Exception {
      final Pageable pageable = PageRequest.of(0, 2);
      final CustomerDtoOutput customerDto =
              new CustomerDtoOutput(customer.getId(), new UserDtoOutput(1L, "userdto", "user@mail.com", Set.of(Role.USER)), customer.getPhoneContact());
      final Page<@NonNull CustomerEntity> entityPage = new PageImpl<>(List.of(customer), pageable, 1);
      final Page<@NonNull CustomerDtoOutput> dtoPage = new PageImpl<>(List.of(customerDto), pageable, 1);

      when(cache.getCache(anyString())).thenReturn(Optional.empty());
      when(customerService.getPage(any(Pageable.class))).thenReturn(entityPage);
      when(mapper.toDto(any(CustomerEntity.class))).thenReturn(customerDto);

      final String jsonExpected = objectMapper.writeValueAsString(dtoPage);
      mockMvc.perform(get(URI_TEMPLATE)
                      .param("page", "0")
                      .param("size", "1")
                      .accept(MediaType.APPLICATION_JSON))
              .andExpectAll(status().isOk(), content().json(jsonExpected));

      final ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
      final ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
      final ArgumentCaptor<CustomerEntity> customerEntityCaptor = ArgumentCaptor.forClass(CustomerEntity.class);

      verify(cache).getCache(keyCaptor.capture());
      verify(customerService).getPage(pageableCaptor.capture());
      verify(mapper).toDto(customerEntityCaptor.capture());
      verifyNoMoreInteractions(customerService, mapper);

      final Pageable pageableGet = pageableCaptor.getValue();
      final CustomerEntity customerGet = customerEntityCaptor.getValue();

      assertNotNull(pageableGet, "The pageable object should not be null");
      assertEquals(1, pageableGet.getPageSize(), "The page size should be 1");
      assertEquals(0, pageableGet.getPageNumber(), "The page number should be 0");
      assertNotNull(customerGet, "CustomerEntity object Should not be null.");
      assertSame(customerGet, customer, "Should be the same entity object.");
   }

   @Test
   @DisplayName("Get Page from cache")
   void getPageFromCache() throws Exception {
      final Pageable pageable = PageRequest.of(0, 2);
      final CustomerEntity customer = CustomerEntity.builder()
              .user(new UserEntity())
              .phoneContact("2244234")
              .build();

      final CustomerDtoOutput customerDto =
              new CustomerDtoOutput(customer.getId(),
                      new UserDtoOutput(1L, "userdto", "user@mail.com",
                              Set.of(Role.USER)),
                      customer.getPhoneContact());
      final Page<@NonNull CustomerDtoOutput> dtoPage = new PageImpl<>(List.of(customerDto), pageable, 1);

      when(cache.getCache(anyString())).thenReturn(Optional.of(dtoPage));

      final String jsonExpected = objectMapper.writeValueAsString(dtoPage);
      mockMvc.perform(get(URI_TEMPLATE)
                      .param("page", "0")
                      .param("size", "1")
                      .accept(MediaType.APPLICATION_JSON))
              .andExpectAll(status().isOk(), content().json(jsonExpected));

      final ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);

      verify(cache).getCache(keyCaptor.capture());
      verifyNoMoreInteractions(cache);
   }


   @Test
   @DisplayName("Should get the customer")
   void getCustomer() throws Exception {
      final long id = 1L;
      final String uri = URI_TEMPLATE + '/' + id;
      final CustomerDtoOutput customerDto =
              new CustomerDtoOutput(id,
                      new UserDtoOutput(1L, "userdto", "user@mail.com",
                              Set.of(Role.USER)),
                      "82048223");
      final CustomerEntity customer = new CustomerEntity(new UserEntity(), "1393141");
      when(customerService.getById(id)).thenReturn(Optional.of(customer));
      when(mapper.toDto(any(CustomerEntity.class))).thenReturn(customerDto);

      final String jsonExpected = objectMapper.writeValueAsString(customerDto);

      mockMvc.perform(get(uri)
                      .param("id", "1")
                      .accept(MediaType.APPLICATION_JSON))
              .andExpectAll(status().isOk(), content().json(jsonExpected));

      final ArgumentCaptor<CustomerEntity> customerCaptor = ArgumentCaptor.forClass(CustomerEntity.class);

      verify(customerService).getById(id);
      verify(mapper).toDto(customerCaptor.capture());

      final CustomerEntity entity = customerCaptor.getValue();
      assertNotNull(entity, "Customer Entity should not be null");
      assertEquals(1L, entity.getId(), "Id should be '1'");
   }

   @Test
   @DisplayName("Should post a customer")
   void postCustomer() throws Exception {
      final CustomerDtoInput customerDtoInput = new CustomerDtoInput("user@mail.com", "24842543");
      final CustomerEntity customerEntity = new CustomerEntity(new UserEntity(), customerDtoInput.contact());
      final CustomerDtoOutput customerDto =
              new CustomerDtoOutput(customerEntity.getId(),
                      new UserDtoOutput(1L, "userdto", "user@mail.com", Set.of(Role.USER)),
                      customerEntity.getPhoneContact());

      when(customerService.save(customerDtoInput)).thenReturn(customerEntity);
      when(mapper.toDto(customerEntity)).thenReturn(customerDto);
      final String jsonBody = objectMapper.writeValueAsString(customerDtoInput);
      final String jsonExpected = objectMapper.writeValueAsString(customerDto);
      mockMvc.perform(post(URI_TEMPLATE)
                      .contentType(MediaType.APPLICATION_JSON)
                      .accept(MediaType.APPLICATION_JSON)
                      .content(jsonBody))
              .andExpectAll(status().isCreated(), content().json(jsonExpected));

      verify(customerService).save(customerDtoInput);
      verify(mapper).toDto(customerEntity);
      verifyNoMoreInteractions(customerService, mapper);
   }

   @Test
   @DisplayName("Should patch the customer")
   void patchContactCustomer() throws Exception {
      final String oldPhone = "213642424";
      final CustomerDtoInput customerDtoInput = new CustomerDtoInput("user@mail.com", "24842543");
      final CustomerEntity customerEntity = new CustomerEntity(new UserEntity(), customerDtoInput.contact());
      final CustomerDtoOutput customerDto =
              new CustomerDtoOutput(customerEntity.getId(),
                      new UserDtoOutput(1L, "userdto", "user@mail.com", Set.of(Role.USER)),
                      customerEntity.getPhoneContact());

      final long ID = customerDto.id();
      final String URI = URI_TEMPLATE + '/' + ID;
      when(customerService.update(ID, customerDtoInput)).thenReturn(customerEntity);
      when(mapper.toDto(customerEntity)).thenReturn(customerDto);

      final String jsonInput = objectMapper.writeValueAsString(customerDtoInput);
      final String jsonExpected = objectMapper.writeValueAsString(customerDto);

      mockMvc.perform(patch(URI)
                      .param("id", "1")
                      .contentType(MediaType.APPLICATION_JSON)
                      .accept(MediaType.APPLICATION_JSON)
                      .content(jsonInput))
              .andExpectAll(status().isOk(), content().json(jsonExpected));

      assertNotNull(customerDto, "Should not be null.");
      assertNotSame(oldPhone, customerDto.contact(), "Should not be the same 'phone'");

      verify(customerService).update(ID, customerDtoInput);
      verify(mapper).toDto(customerEntity);
      verifyNoMoreInteractions(customerService, mapper);
   }

   @Test
   @DisplayName("Should delete the customer")
   void deleteCustomer() throws Exception {
      final long ID = 1L;
      final String URI = URI_TEMPLATE + '/' + ID;

      mockMvc.perform(delete(URI)
                      .param("id", "1"))
              .andExpect(status().isNoContent());

      verify(customerService).softDelete(ID);
      verifyNoMoreInteractions(customerService, mapper);
   }
}