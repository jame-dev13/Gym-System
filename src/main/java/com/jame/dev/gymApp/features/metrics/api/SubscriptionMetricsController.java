package com.jame.dev.gymApp.features.metrics.api;

import com.jame.dev.gymApp.features.metrics.api.response.MembershipRanking;
import com.jame.dev.gymApp.features.metrics.api.response.PeriodRankingPerYear;
import com.jame.dev.gymApp.features.metrics.api.response.TotalSubscriptions;
import com.jame.dev.gymApp.features.metrics.application.contract.SubscriptionMetricsService;
import com.jame.dev.gymApp.features.metrics.domain.model.SubsPerMembership;
import com.jame.dev.gymApp.features.metrics.domain.model.SubsPerMonthDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

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
   public ResponseEntity<List<MembershipRanking>> getMembershipRankings() {
      return ResponseEntity.ok(service.getMembershipRanking());
   }

   @GetMapping("/rankings/periods")
   public ResponseEntity<List<PeriodRankingPerYear>> getPeriodRankings() {
      return ResponseEntity.ok(service.getPeriodRanking());
   }

   @GetMapping("/{date}/before")
   public ResponseEntity<TotalSubscriptions> getTotalBefore(@PathVariable("date") final LocalDate date) {
      return ResponseEntity.ok(service.getSubscriptionsBefore(date));
   }

   @GetMapping("/memberships")
   public ResponseEntity<List<SubsPerMembership>> getSubsPerMembership() {
      return ResponseEntity.ok(service.getSubscriptionsPerMembership());
   }

   @GetMapping("/months")
   public ResponseEntity<List<SubsPerMonthDto>> getSubsPerMonth() {
      return ResponseEntity.ok(service.getSubscriptionsPerMonth());
   }
}
