package com.jame.dev.gymApp.features.metrics.api;

import com.jame.dev.gymApp.features.metrics.api.response.AnnualResumeResponse;
import com.jame.dev.gymApp.features.metrics.api.response.InvestmentMonthEvolutionResponse;
import com.jame.dev.gymApp.features.metrics.api.response.TotalInvestment;
import com.jame.dev.gymApp.features.metrics.application.contract.PaymentMetricsSubscriberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/app/v1/subscribers/metrics")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class PaymentSubscriberMetricsController {

   private final PaymentMetricsSubscriberService paymentMetricsService;

   @PreAuthorize("@customerSecurity.isOwner(#customerId, authentication)")
   @GetMapping("/{customerId}/investments")
   public ResponseEntity<TotalInvestment> getTotalInvestment(
      @PathVariable("customerId") long customerId
   ) {
      return ResponseEntity.ok(paymentMetricsService.getTotalExpend(customerId));
   }

   @PreAuthorize("@customerSecurity.isOwner(#customerId, authentication)")
   @GetMapping("/{customerId}/resumes")
   public ResponseEntity<AnnualResumeResponse> getAnnualResume(
      @PathVariable("customerId") long customerId
   ) {
      return ResponseEntity.ok(paymentMetricsService.getAnnualResume(customerId));
   }

   @PreAuthorize("@customerSecurity.isOwner(#customerId, authentication)")
   @GetMapping("/{customerId}/evolution")
   public ResponseEntity<InvestmentMonthEvolutionResponse> getMonthInvestmentEvolution(
      @PathVariable("customerId") long customerId
   ) {
      return ResponseEntity.ok(paymentMetricsService.getInvestmentMonthEvolution(customerId));
   }
}
