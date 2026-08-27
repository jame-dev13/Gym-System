package com.jame.dev.gymApp.features.metrics.api;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.metrics.api.response.AnnualResumeResponse;
import com.jame.dev.gymApp.features.metrics.api.response.InvestmentMonthEvolutionResponse;
import com.jame.dev.gymApp.features.metrics.application.contract.PaymentMetricsSubscriberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/app/v1/subscribers/metrics/billings/current")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class PaymentSubscriberMetricsController {
   private final PaymentMetricsSubscriberService paymentMetricsService;

   @GetMapping("/resume")
   public ResponseEntity<AnnualResumeResponse> getAnnualResume(final @AuthenticationPrincipal AuthPrincipal principal) {
      return ResponseEntity.ok(paymentMetricsService.getAnnualResume(principal));
   }

   @GetMapping("/evolution")
   public ResponseEntity<InvestmentMonthEvolutionResponse> getMonthInvestmentEvolution(final @AuthenticationPrincipal AuthPrincipal principal) {
      return ResponseEntity.ok(paymentMetricsService.getInvestmentMonthEvolution(principal));
   }
}
