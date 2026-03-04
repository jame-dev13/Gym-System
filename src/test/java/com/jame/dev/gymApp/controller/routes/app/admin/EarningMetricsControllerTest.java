package com.jame.dev.gymApp.controller.routes.app.admin;

import com.jame.dev.gymApp.auth.filters.CustomAuthorizationFilter;
import com.jame.dev.gymApp.controller.advice.ApiErrorResponseFactory;
import com.jame.dev.gymApp.metrics.service.in.EarningMetricsService;
import com.jame.dev.gymApp.model.dto.out.MonthTotal;
import com.jame.dev.gymApp.model.metrics.TotalPerMembershipTypeDto;
import com.jame.dev.gymApp.shared.enums.Membership;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EarningMetricsController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = CustomAuthorizationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class EarningMetricsControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @MockitoBean
   private ApiErrorResponseFactory responseFactory;

   @MockitoBean
   private EarningMetricsService service;

   private final String URI_TEMPLATE = "/app/v1/administration/metrics/earnings";

   @Test
   @DisplayName("Should return the total earned.")
   void getTotalEarnings() throws Exception {
      final String URI = URI_TEMPLATE + "/total";
      when(service.getTotal()).thenReturn(BigDecimal.valueOf(10_000d));

      mockMvc.perform(get(URI)
                      .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.total").value(BigDecimal.valueOf(10_000d)));

      verify(service, atLeastOnce()).getTotal();
      verifyNoMoreInteractions(service);
   }

   @Test
   @DisplayName("Should return the list of months and his total earned.")
   void getTotalPerMonth() throws Exception {
      final String URI = URI_TEMPLATE + "/months";
      var returnData = Map.of(2026, List.of(
              new MonthTotal( "December", BigDecimal.valueOf(3_000d))));
      when(service.getTotalPerMonth()).thenReturn(returnData);

      this.mockMvc.perform(get(URI)
                      .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.*").exists());
      verify(service, atLeastOnce()).getTotalPerMonth();
      verifyNoMoreInteractions(service);
   }

   @Test
   @DisplayName("Should return the list of memberships and his total earned")
   void getTotalPerMembershipType() throws Exception {
      final String URI = URI_TEMPLATE + "/memberships";
      when(service.getTotalPerMembershipType()).thenReturn(List.of(
              new TotalPerMembershipTypeDto(Membership.MONTHLY, BigDecimal.valueOf(3_000d))
      ));

      this.mockMvc.perform(get(URI)
                      .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.[0]").exists());
      verify(service, atLeastOnce()).getTotalPerMembershipType();
      verifyNoMoreInteractions(service);
   }
}