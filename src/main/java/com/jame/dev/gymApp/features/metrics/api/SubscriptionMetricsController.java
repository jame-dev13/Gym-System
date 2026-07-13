package com.jame.dev.gymApp.features.metrics.api;

import com.jame.dev.gymApp.features.metrics.api.response.MembershipRankingsResponse;
import com.jame.dev.gymApp.features.metrics.api.response.PeriodRankingsResponse;
import com.jame.dev.gymApp.features.metrics.api.response.SubscriptionsPerMembershipResponse;
import com.jame.dev.gymApp.features.metrics.api.response.SubscriptionsPerMonthResponse;
import com.jame.dev.gymApp.features.metrics.api.response.TotalSubscriptions;
import com.jame.dev.gymApp.features.metrics.application.contract.SubscriptionMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/app/v1/administration/metrics/subs")
@RequiredArgsConstructor
public class SubscriptionMetricsController {

   private final SubscriptionMetricsService service;

   @GetMapping("/totals")
   public ResponseEntity<TotalSubscriptions> getTotalSubscriptions() {
      return ResponseEntity.ok(service.getTotalSubscriptions());
   }

   @GetMapping("/rankings/memberships")
   public ResponseEntity<MembershipRankingsResponse> getMembershipRankings() {
      return ResponseEntity.ok(service.getMembershipRanking());
   }

   @GetMapping("/rankings/periods")
   public ResponseEntity<PeriodRankingsResponse> getPeriodRankings() {
      return ResponseEntity.ok(service.getPeriodRanking());
   }

   @GetMapping("/{date}/before")
   public ResponseEntity<TotalSubscriptions> getTotalBefore(@PathVariable("date") final LocalDate date) {
      return ResponseEntity.ok(service.getSubscriptionsBefore(date));
   }

   @GetMapping("/memberships")
   public ResponseEntity<SubscriptionsPerMembershipResponse> getSubsPerMembership() {
      return ResponseEntity.ok(service.getSubscriptionsPerMembership());
   }

   @GetMapping("/months")
   public ResponseEntity<SubscriptionsPerMonthResponse> getSubsPerMonth() {
      return ResponseEntity.ok(service.getSubscriptionsPerMonth());
   }
}
