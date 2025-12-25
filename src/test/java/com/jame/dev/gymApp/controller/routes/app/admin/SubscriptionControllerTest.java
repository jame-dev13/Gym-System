package com.jame.dev.gymApp.controller.routes.app.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jame.dev.gymApp.auth.filters.CustomAuthorizationFilter;
import com.jame.dev.gymApp.cache.service.AppCacheService;
import com.jame.dev.gymApp.controller.advice.ApiErrorResponseFactory;
import com.jame.dev.gymApp.entity.*;
import com.jame.dev.gymApp.mapper.*;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.model.dto.out.SubscriptionDtoOutput;
import com.jame.dev.gymApp.service.in.SubscriptionService;
import com.jame.dev.gymApp.shared.enums.Membership;
import com.jame.dev.gymApp.shared.enums.Period;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = SubscriptionController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = CustomAuthorizationFilter.class)
        })
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private SubscriptionController controller;

   @MockitoBean
   private SubscriptionService service;

   @MockitoBean
   private BaseMapper<SubscriptionEntity, SubscriptionDtoOutput> mapper;

   @MockitoBean
   private AppCacheService<SubscriptionDtoOutput> cache;

   @MockitoBean
   private ApiErrorResponseFactory responseFactory;

   @Captor
   private ArgumentCaptor<String> keyCaptor;

   @Captor
   private ArgumentCaptor<Pageable> pageableCaptor;

   @Captor
   private ArgumentCaptor<SubscriptionEntity> subscriptionCaptor;

   @Captor
   private ArgumentCaptor<Page<@NonNull SubscriptionDtoOutput>> pageDtoCaptor;

   @Captor
   private ArgumentCaptor<SubscriptionDtoInput> dtoInputCaptor;

   private final String URI_TEMPLATE = "/admin/subscriptions";

   private final CustomerEntity customer = new CustomerEntity(1L, new UserEntity(), "2744232", true);
   private final PricingEntity pricing = new PricingEntity(1, new MemberShipEntity(1, Membership.MONTHLY), BigDecimal.valueOf(300.0d));
   private final PeriodEntity period = new PeriodEntity(Period.MONTHLY, LocalDate.now());
   private final SubscriptionEntity subscriptionEntity = SubscriptionEntity.builder()
           .id(1L)
           .customer(customer)
           .pricing(pricing)
           .subscriptionPeriods(List.of(period))
           .active(true)
           .finished(false)
           .build();
   private final SubscriptionDtoOutput dto = mapToDto(subscriptionEntity);
   private final SubscriptionDtoInput dtoInput = new SubscriptionDtoInput(1L, 1);
   private final ObjectMapper objectMapper = new ObjectMapper()
           .registerModule(new JavaTimeModule());

   @BeforeEach
   void resetControllerState() {
      ReflectionTestUtils.setField(controller, "currentPageKey", null);
      Map<?, ?> cacheOnes =
              (Map<?, ?>) ReflectionTestUtils.getField(controller, "cacheOnes");
      cacheOnes.clear();

   }


   @Test
   @DisplayName("[GET] Should get the page.")
   void getSubscriptionPage() throws Exception {
      final Sort sortEntity = Sort.sort(SubscriptionEntity.class).by(SubscriptionEntity::getId).ascending();
      final Sort sortDto = Sort.by(Sort.Direction.ASC, "id");

      final Pageable pageableEntity = PageRequest.of(0, 1, sortEntity);
      final Pageable pageableDto = PageRequest.of(0, 1, sortDto);

      final Page<@NonNull SubscriptionEntity> entityPage = new PageImpl<>(List.of(subscriptionEntity), pageableEntity, 1);
      final Page<@NonNull SubscriptionDtoOutput> dtoPage = new PageImpl<>(List.of(dto), pageableDto, 1);

      when(cache.getCache(anyString())).thenReturn(Optional.empty());
      when(service.getPage(any(Pageable.class))).thenReturn(entityPage);
      when(mapper.toDto(any(SubscriptionEntity.class))).thenReturn(dto);

      mockMvc.perform(get(URI_TEMPLATE)
                      .param("page", "0")
                      .param("size", "1")
                      .accept(MediaType.APPLICATION_JSON))
              .andExpectAll(
                      status().isOk());

      verify(cache).getCache(keyCaptor.capture());
      verify(service).getPage(pageableCaptor.capture());
      verify(mapper).toDto(subscriptionCaptor.capture());
      verify(cache, atLeastOnce()).saveCache(keyCaptor.capture(), pageDtoCaptor.capture());
   }

   @Test
   @DisplayName("[GET] Should get subscription.")
   void getSubscription() throws Exception {
      when(service.getById(1L)).thenReturn(Optional.of(subscriptionEntity));
      when(mapper.toDto(any(SubscriptionEntity.class))).thenReturn(dto);

      mockMvc.perform(get(URI_TEMPLATE + '/' + 1L)
                      .param("id", String.valueOf(1L))
                      .accept(MediaType.APPLICATION_JSON))
              .andExpectAll(
                      status().isOk())
              .andDo(print());

      verify(service, atLeastOnce()).getById(1L);
      verify(mapper, atLeastOnce()).toDto(subscriptionCaptor.capture());
   }

   @Test
   @DisplayName("[CACHE - GET] should get Page from cache")
   void getPageFromCache() throws Exception {
      setCurrentPageKey();

      final Sort sortDto = Sort.by(Sort.Direction.ASC, "id");
      final Pageable pageableDto = PageRequest.of(0, 1, sortDto);
      final Page<@NonNull SubscriptionDtoOutput> dtoPage = new PageImpl<>(List.of(dto), pageableDto, 1);
      when(cache.getCache(anyString())).thenReturn(Optional.of(dtoPage));

      mockMvc.perform(get(URI_TEMPLATE)
                      .param("page", "0")
                      .param("size", "1")
                      .accept(MediaType.APPLICATION_JSON))
              .andExpectAll(
                      status().isOk());

      verify(cache, atLeastOnce()).getCache(keyCaptor.capture());
      verifyNoInteractions(service);
   }

   @Test
   @DisplayName("[POST] Should post subscriptions")
   void postSubscription() throws Exception {
      when(service.save(any(SubscriptionDtoInput.class))).thenReturn(subscriptionEntity);
      when(mapper.toDto(any(SubscriptionEntity.class))).thenReturn(dto);

      final String jsonInput = objectMapper.writeValueAsString(dtoInput);

      mockMvc.perform(post(URI_TEMPLATE)
                      .contentType(MediaType.APPLICATION_JSON)
                      .accept(MediaType.APPLICATION_JSON)
                      .content(jsonInput))
              .andExpectAll(
                      status().isCreated());

      verify(service, atLeastOnce()).save(dtoInputCaptor.capture());
      verify(mapper, atLeastOnce()).toDto(subscriptionCaptor.capture());
   }

   @Test
   @DisplayName("[PATCH] Should finalize the given subscription")
   void finalizeSubscription() throws Exception {
      subscriptionEntity.setFinished(true);
      SubscriptionDtoOutput subscriptionDtoOutput = mapToDto(subscriptionEntity);

      when(service.patch(eq(1L))).thenReturn(subscriptionEntity);
      when(mapper.toDto(any(SubscriptionEntity.class))).thenReturn(subscriptionDtoOutput);

      final String jsonExpected = objectMapper.writeValueAsString(subscriptionDtoOutput);

      mockMvc.perform(patch(URI_TEMPLATE + '/' + 1L)
                      .param("id", String.valueOf(1L))
                      .accept(MediaType.APPLICATION_JSON))
              .andExpectAll(
                      status().isOk());

      verify(service, atLeastOnce()).patch(eq(1L));
      verify(mapper, atLeastOnce()).toDto(subscriptionCaptor.capture());

      assertTrue(subscriptionEntity.isFinished(), "Should be finished.");
   }

   @Test
   @DisplayName("[DELETE] Should delete a resource and invalidate caches.")
   void deleteSubscription() throws Exception {
      setCurrentPageKey();

      when(cache.keyExists(anyString())).thenReturn(true);

      mockMvc.perform(delete(URI_TEMPLATE + '/' + 1L)
                      .param("id", String.valueOf(1L)))
              .andExpectAll(
                      status().isNoContent()
              );

      verify(cache, times(1)).keyExists(keyCaptor.capture());
      verify(service, times(1)).softDelete(eq(1L));
      verify(cache, times(1)).invalidatePage(keyCaptor.capture());
   }

   private SubscriptionDtoOutput mapToDto(SubscriptionEntity subscription) {
      final UserMapper userMapper = new UserMapperImpl(new RoleMapperImpl());
      final CustomerMapper customerMapper = new CustomerMapperImpl(userMapper);
      final PeriodMapper periodMapper = new PeriodMapperImpl();
      final SubscriptionMapper subscriptionMapper = new SubscriptionMapperImpl(customerMapper, periodMapper);
      return subscriptionMapper
              .toDto(subscription);
   }

   private void setCurrentPageKey() {
      final String key = "subscriptions:0:1";
      ReflectionTestUtils.setField(controller, "currentPageKey", key);
   }
}