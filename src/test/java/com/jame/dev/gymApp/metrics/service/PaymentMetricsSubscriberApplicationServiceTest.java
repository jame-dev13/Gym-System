package com.jame.dev.gymApp.metrics.service;

import com.jame.dev.gymApp.features.metrics.api.response.AnnualResumeResponse;
import com.jame.dev.gymApp.features.metrics.api.response.InvestmentMonthEvolutionResponse;
import com.jame.dev.gymApp.features.metrics.application.service.PaymentMetricsSubscriberApplicationService;
import com.jame.dev.gymApp.features.metrics.domain.model.MonthTotal;
import com.jame.dev.gymApp.features.metrics.domain.repository.PaymentMetricsRepository;
import com.jame.dev.gymApp.features.auth.infrastructure.security.identity.IdentityExtractorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentMetricsSubscriberApplicationServiceTest {

   private static final String SUBJECT = "user@mail.com";

   @Mock
   private PaymentMetricsRepository paymentMetricsRepository;

   @Mock
   private IdentityExtractorService identityExtractorService;

   @Mock
   private Authentication authentication;

   @InjectMocks
   private PaymentMetricsSubscriberApplicationService service;

   @Captor
   private ArgumentCaptor<String> subjectCaptor;

   @Test
   @DisplayName("Should get the annual resume of payments for the authenticated user")
   void getAnnualResume() {
      final AnnualResumeResponse expected = new AnnualResumeResponse(
         12L, BigDecimal.valueOf(15_000d), BigDecimal.valueOf(1_250d), 8L, 4L);

      when(identityExtractorService.extract(authentication)).thenReturn(SUBJECT);
      when(paymentMetricsRepository.calculateAnnualResume(SUBJECT)).thenReturn(expected);

      final AnnualResumeResponse result = service.getAnnualResume(authentication);

      verify(identityExtractorService, times(1)).extract(authentication);
      verify(paymentMetricsRepository, times(1)).calculateAnnualResume(subjectCaptor.capture());
      verifyNoMoreInteractions(paymentMetricsRepository, identityExtractorService);

      assertEquals(SUBJECT, subjectCaptor.getValue(), "Repo should be called with the extracted subject");
      assertNotNull(result, "Result should not be null");
   }

   @Test
   @DisplayName("Should return null when the repo has no annual resume data")
   void getAnnualResume_whenRepoReturnsNull() {
      when(identityExtractorService.extract(authentication)).thenReturn(SUBJECT);
      when(paymentMetricsRepository.calculateAnnualResume(SUBJECT)).thenReturn(null);

      final AnnualResumeResponse result = service.getAnnualResume(authentication);

      verify(identityExtractorService, times(1)).extract(authentication);
      verify(paymentMetricsRepository, times(1)).calculateAnnualResume(SUBJECT);
      verifyNoMoreInteractions(paymentMetricsRepository, identityExtractorService);

      assertNull(result, "Result should be null when the repo returns null");
   }

   @Test
   @DisplayName("Should forward the subject extracted from the authentication to the repo")
   void getAnnualResume_subjectForwarded() {
      when(identityExtractorService.extract(authentication)).thenReturn(SUBJECT);
      when(paymentMetricsRepository.calculateAnnualResume(any(String.class)))
         .thenReturn(new AnnualResumeResponse(0L, BigDecimal.ZERO, BigDecimal.ZERO, 0L, 0L));

      final AnnualResumeResponse result = service.getAnnualResume(authentication);

      verify(identityExtractorService, times(1)).extract(authentication);
      verify(paymentMetricsRepository, times(1)).calculateAnnualResume(subjectCaptor.capture());
      verifyNoMoreInteractions(paymentMetricsRepository, identityExtractorService);

      assertEquals(SUBJECT, subjectCaptor.getValue(), "Subject passed to the repo should match the extracted one");
      assertNotNull(result, "Result should not be null");
   }

   @Test
   @DisplayName("Should get the investment evolution along months for the authenticated user")
   void getInvestmentMonthEvolution() {
      final List<MonthTotal> months = List.of(
         new MonthTotal("Jan", BigDecimal.valueOf(1_000d)),
         new MonthTotal("Feb", BigDecimal.valueOf(2_000d)));

      when(identityExtractorService.extract(authentication)).thenReturn(SUBJECT);
      when(paymentMetricsRepository.calculatePaymentEvolutionAlongMonths(SUBJECT)).thenReturn(months);

      final InvestmentMonthEvolutionResponse result = service.getInvestmentMonthEvolution(authentication);

      verify(identityExtractorService, times(1)).extract(authentication);
      verify(paymentMetricsRepository, times(1)).calculatePaymentEvolutionAlongMonths(subjectCaptor.capture());
      verifyNoMoreInteractions(paymentMetricsRepository, identityExtractorService);

      assertEquals(SUBJECT, subjectCaptor.getValue(), "Repo should be called with the extracted subject");
      assertNotNull(result, "Response should not be null");
      assertNotNull(result.content(), "Content should not be null");
      assertEquals(months, result.content(), "Content should match the repo result");
   }

   @Test
   @DisplayName("Should return a response with empty content when there are no payments")
   void getInvestmentMonthEvolution_whenNoPayments() {
      when(identityExtractorService.extract(authentication)).thenReturn(SUBJECT);
      when(paymentMetricsRepository.calculatePaymentEvolutionAlongMonths(SUBJECT)).thenReturn(List.of());

      final InvestmentMonthEvolutionResponse result = service.getInvestmentMonthEvolution(authentication);

      verify(identityExtractorService, times(1)).extract(authentication);
      verify(paymentMetricsRepository, times(1)).calculatePaymentEvolutionAlongMonths(SUBJECT);
      verifyNoMoreInteractions(paymentMetricsRepository, identityExtractorService);

      assertNotNull(result, "Response should not be null");
      assertNotNull(result.content(), "Content should not be null");
      assertTrue(result.content().isEmpty(), "Content should be empty when the repo returns no payments");
   }

   @Test
   @DisplayName("Should return a response with null content when the repo returns null")
   void getInvestmentMonthEvolution_whenRepoReturnsNull() {
      when(identityExtractorService.extract(authentication)).thenReturn(SUBJECT);
      when(paymentMetricsRepository.calculatePaymentEvolutionAlongMonths(SUBJECT)).thenReturn(null);

      final InvestmentMonthEvolutionResponse result = service.getInvestmentMonthEvolution(authentication);

      verify(identityExtractorService, times(1)).extract(authentication);
      verify(paymentMetricsRepository, times(1)).calculatePaymentEvolutionAlongMonths(SUBJECT);
      verifyNoMoreInteractions(paymentMetricsRepository, identityExtractorService);

      assertNotNull(result, "Response should not be null");
      assertNull(result.content(), "Content should be null when the repo returns null");
   }
}