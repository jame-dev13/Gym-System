package com.jame.dev.gymApp.metrics.controller;

import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationNullException;
import com.jame.dev.gymApp.features.auth.infrastructure.security.CustomAuthorizationFilter;
import com.jame.dev.gymApp.features.metrics.api.PaymentSubscriberMetricsController;
import com.jame.dev.gymApp.features.metrics.api.response.AnnualResumeResponse;
import com.jame.dev.gymApp.features.metrics.api.response.InvestmentMonthEvolutionResponse;
import com.jame.dev.gymApp.features.metrics.application.contract.PaymentMetricsSubscriberService;
import com.jame.dev.gymApp.features.metrics.domain.model.MonthTotal;
import com.jame.dev.gymApp.presentation.exception.GlobalExceptionHandler;
import config.TestConfig;
import config.TestValidationConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
   controllers = PaymentSubscriberMetricsController.class,
   excludeFilters = {
      @ComponentScan.Filter(
         type = FilterType.ASSIGNABLE_TYPE,
         classes = CustomAuthorizationFilter.class
      )}
)
@AutoConfigureMockMvc(addFilters = false)
@Import({
   GlobalExceptionHandler.class,
   TestValidationConfig.class,
   TestConfig.class})
@ImportAutoConfiguration({ValidationAutoConfiguration.class})
class PaymentSubscriberMetricsControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @MockitoBean
   private PaymentMetricsSubscriberService paymentMetricsService;

   private final String URI_TEMPLATE = "/app/v1/subscribers/metrics/billings/current";

   private final AnnualResumeResponse annualResumeResponse = new AnnualResumeResponse(
      12L, BigDecimal.valueOf(15_000d), BigDecimal.valueOf(1_250d), 8L, 4L);

   private final InvestmentMonthEvolutionResponse evolutionResponse = new InvestmentMonthEvolutionResponse(
      List.of(new MonthTotal("Jan", BigDecimal.valueOf(1_000d))));

   @Nested
   @DisplayName("GET: /app/v1/subscribers/metrics/billings/current/resume")
   class PaymentSubscriberMetricsGetResumeTests {

      @Test
      @DisplayName("GET[200] OK: Annual resume of the authenticated user")
      void getAnnualResume() throws Exception {
         given(paymentMetricsService.getAnnualResume(any())).willReturn(annualResumeResponse);

         mockMvc.perform(get(URI_TEMPLATE + "/resume")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists())
            .andExpectAll(
               jsonPath("$.totalPaymentsMade").value(12),
               jsonPath("$.totalExpend").value(15000),
               jsonPath("$.average").value(1250),
               jsonPath("$.electronicPaymentsDone").value(8),
               jsonPath("$.physicPaymentsDone").value(4)
            );

         verify(paymentMetricsService, times(1)).getAnnualResume(any());
         verifyNoMoreInteractions(paymentMetricsService);
      }

      @Test
      @DisplayName("GET[401] Unauthorized: No authenticated user in session")
      void getAnnualResumeUnauthenticated() throws Exception {
         given(paymentMetricsService.getAnnualResume(any())).willThrow(AuthenticationNullException.class);

         mockMvc.perform(get(URI_TEMPLATE + "/resume")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.code").value("AUTHENTICATION_OPERATION"));

         verify(paymentMetricsService, times(1)).getAnnualResume(any());
      }
   }

   @Nested
   @DisplayName("GET: /app/v1/subscribers/metrics/billings/current/evolution")
   class PaymentSubscriberMetricsGetEvolutionTests {

      @Test
      @DisplayName("GET[200] OK: Investment evolution along months of the authenticated user")
      void getMonthInvestmentEvolution() throws Exception {
         given(paymentMetricsService.getInvestmentMonthEvolution(any())).willReturn(evolutionResponse);

         mockMvc.perform(get(URI_TEMPLATE + "/evolution")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].month").value("Jan"))
            .andExpect(jsonPath("$.content[0].total").value(1000));

         verify(paymentMetricsService, times(1)).getInvestmentMonthEvolution(any());
         verifyNoMoreInteractions(paymentMetricsService);
      }

      @Test
      @DisplayName("GET[200] OK: Empty evolution when there are no payments")
      void getMonthInvestmentEvolutionEmptyContent() throws Exception {
         given(paymentMetricsService.getInvestmentMonthEvolution(any()))
            .willReturn(new InvestmentMonthEvolutionResponse(List.of()));

         mockMvc.perform(get(URI_TEMPLATE + "/evolution")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content").isEmpty());

         verify(paymentMetricsService, times(1)).getInvestmentMonthEvolution(any());
         verifyNoMoreInteractions(paymentMetricsService);
      }

      @Test
      @DisplayName("GET[401] Unauthorized: No authenticated user in session")
      void getMonthInvestmentEvolutionUnauthenticated() throws Exception {
         given(paymentMetricsService.getInvestmentMonthEvolution(any()))
            .willThrow(AuthenticationNullException.class);

         mockMvc.perform(get(URI_TEMPLATE + "/evolution")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.code").value("AUTHENTICATION_OPERATION"));

         verify(paymentMetricsService, times(1)).getInvestmentMonthEvolution(any());
      }
   }
}