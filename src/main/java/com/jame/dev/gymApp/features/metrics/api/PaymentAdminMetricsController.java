package com.jame.dev.gymApp.features.metrics.api;

import com.jame.dev.gymApp.features.metrics.api.response.AnnualResumeResponse;
import com.jame.dev.gymApp.features.metrics.api.response.InvestmentMonthEvolutionResponse;
import com.jame.dev.gymApp.features.metrics.application.contract.PaymentMetricsAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/v1/administration/metrics/payments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PaymentAdminMetricsController {

   private final PaymentMetricsAdminService paymentMetricsAdminService;

   @GetMapping("/resume")
   public ResponseEntity<AnnualResumeResponse> getAnnualResume() {
      return ResponseEntity.ok(paymentMetricsAdminService.getAnnualResume());
   }

   @GetMapping("/evolution")
   public ResponseEntity<InvestmentMonthEvolutionResponse> getMonthInvestmentEvolution() {
      return ResponseEntity.ok(paymentMetricsAdminService.getInvestmentMonthEvolution());
   }
}
