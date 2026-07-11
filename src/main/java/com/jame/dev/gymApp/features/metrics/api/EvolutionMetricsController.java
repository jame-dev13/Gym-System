package com.jame.dev.gymApp.features.metrics.api;


import com.jame.dev.gymApp.features.metrics.api.response.BillingEvolutionResponse;
import com.jame.dev.gymApp.features.metrics.api.response.CustomerEvolutionResponse;
import com.jame.dev.gymApp.features.metrics.api.response.SubscriberEvolutionResponse;
import com.jame.dev.gymApp.features.metrics.application.contract.EvolutionMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/v1/administration/metrics/evolutions")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class EvolutionMetricsController {

   private final EvolutionMetricsService evolutionMetricsService;

   @GetMapping("/{year}/billings")
   public ResponseEntity<BillingEvolutionResponse> getBillingEvolution(final @PathVariable int year) {
      return ResponseEntity.ok(evolutionMetricsService.getBillingEvolution(year));
   }

   @GetMapping("/{year}/customers/downs")
   public ResponseEntity<CustomerEvolutionResponse> getDowningCustomersEvolution(final @PathVariable int year) {
      return ResponseEntity.ok(evolutionMetricsService.getDowningCustomerEvolution(year));
   }

   @GetMapping("/{year}/customers/joins")
   public ResponseEntity<CustomerEvolutionResponse> getJoiningCustomersEvolution(final @PathVariable int year) {
      return ResponseEntity.ok(evolutionMetricsService.getJoiningCustomerEvolution(year));
   }

   @GetMapping("/{year}/subscribers/joins")
   public ResponseEntity<SubscriberEvolutionResponse> getJoiningSubscribersEvolution(final @PathVariable int year) {
      return ResponseEntity.ok(evolutionMetricsService.getJoiningSubscriberEvolution(year));
   }

   @GetMapping("/{year}/subscribers/downs")
   public ResponseEntity<SubscriberEvolutionResponse> getDowningSubscribersBeforeReachEndDate(final @PathVariable int year) {
      return ResponseEntity.ok(evolutionMetricsService.getDowningSubscribersBeforeEndTime(year));
   }
}
