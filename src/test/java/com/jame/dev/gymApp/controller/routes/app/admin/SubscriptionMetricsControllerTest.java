package com.jame.dev.gymApp.controller.routes.app.admin;

import com.jame.dev.gymApp.features.auth.infrastructure.security.CustomAuthorizationFilter;
import com.jame.dev.gymApp.features.metrics.api.SubscriptionMetricsController;
import com.jame.dev.gymApp.presentation.exception.ApiErrorResponseFactory;
import com.jame.dev.gymApp.features.metrics.application.contract.SubscriptionMetricsService;
import com.jame.dev.gymApp.features.metrics.domain.model.SubsPerMembership;
import com.jame.dev.gymApp.features.metrics.domain.model.SubsPerMonthDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = SubscriptionMetricsController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomAuthorizationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class SubscriptionMetricsControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @MockitoBean
   private ApiErrorResponseFactory responseFactory;

   @MockitoBean
   private SubscriptionMetricsService service;

   private final String URI_TEMPLATE = "/app/v1/administration/metrics/subs";

   @Test
   @DisplayName("Should return the total subscriptions active and unfinished.")
   void getTotalSubscriptions() throws Exception {
      final String URI = URI_TEMPLATE + "/totals";
      when(service.getTotalSubscriptions()).thenReturn(10L);

      mockMvc.perform(get(URI)
              .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.total").value(10L));

      verify(service, atLeastOnce()).getTotalSubscriptions();
      verifyNoMoreInteractions(service);
   }

   @Test
   @DisplayName("Should return the total subscription made before a given date.")
   void getTotalBefore() throws Exception {
      final String URI = URI_TEMPLATE + '/' + LocalDate.now() + "/before";
      when(service.getSubscriptionsBefore(any(LocalDate.class)))
              .thenReturn(2L);

      mockMvc.perform(get(URI)
              .param("date", LocalDate.now().toString())
              .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.total").value(2L));
      verify(service, atLeastOnce()).getSubscriptionsBefore(any(LocalDate.class));
      verifyNoMoreInteractions(service);
   }


   @Test
   @DisplayName("Should return a list of subs grouped by memberships.")
   void getSubsPerMembership() throws Exception {
      final String URI = URI_TEMPLATE + "/memberships";
      when(service.getSubscriptionsPerMembership()).thenReturn(List.of(
              new SubsPerMembership("MONTHLY", 2L)
      ));

      mockMvc.perform(get(URI)
              .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.[0]").exists());
      verify(service, atLeastOnce()).getSubscriptionsPerMembership();
      verifyNoMoreInteractions(service);
   }

   @Test
   @DisplayName("Should return a list of subs grouped by months.")
   void getSubsPerMonth() throws Exception {
      final String URI = URI_TEMPLATE + "/months";
      when(service.getSubscriptionsPerMonth()).thenReturn(List.of(
              new SubsPerMonthDto("December", 10L)
      ));

      mockMvc.perform(get(URI)
              .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.[0]").exists());
      verify(service, atLeastOnce()).getSubscriptionsPerMonth();
      verifyNoMoreInteractions(service);
   }
}