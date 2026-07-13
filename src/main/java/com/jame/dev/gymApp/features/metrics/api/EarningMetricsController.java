package com.jame.dev.gymApp.features.metrics.api;

import com.jame.dev.gymApp.features.metrics.api.response.EarningsByMembershipTypeResponse;
import com.jame.dev.gymApp.features.metrics.api.response.EarningsByMonthResponse;
import com.jame.dev.gymApp.features.metrics.api.response.PeriodicalEarningsResponse;
import com.jame.dev.gymApp.features.metrics.api.response.TotalEarned;
import com.jame.dev.gymApp.features.metrics.application.contract.EarningMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/v1/administration/metrics/earnings")
@RequiredArgsConstructor
public class EarningMetricsController {
   private final EarningMetricsService service;

   @GetMapping("/total")
   public ResponseEntity<TotalEarned> getTotalEarnings() {
      return ResponseEntity.ok(service.getTotal());
   }

   @GetMapping("/months")
   public ResponseEntity<EarningsByMonthResponse> getTotalPerMonth() {
      return ResponseEntity.ok(service.getTotalPerMonth());
   }

   @GetMapping("/memberships")
   public ResponseEntity<EarningsByMembershipTypeResponse> getTotalPerMembershipType() {
      return ResponseEntity.ok(service.getTotalPerMembershipType());
   }

   @GetMapping("/rankings/periods")
   public ResponseEntity<PeriodicalEarningsResponse> getPeriodicalEarningsByYearRanking() {
      return ResponseEntity.ok(service.getPeriodicalEarnings());
   }
}
