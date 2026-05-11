package com.jame.dev.gymApp.features.metrics.api;

import com.jame.dev.gymApp.features.metrics.application.contract.EarningMetricsService;
import com.jame.dev.gymApp.features.metrics.domain.model.MonthTotal;
import com.jame.dev.gymApp.features.metrics.domain.model.TotalPerMembershipTypeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app/v1/administration/metrics/earnings")
@RequiredArgsConstructor
public class EarningMetricsController {
   private final EarningMetricsService service;

   @GetMapping("/total")
   public ResponseEntity<Map<String, BigDecimal>> getTotalEarnings() {
      return ResponseEntity.ok(Map.of("total", service.getTotal()));
   }

   @GetMapping("/months")
   public ResponseEntity<Map<Integer, List<MonthTotal>>> getTotalPerMonth() {
      return ResponseEntity.ok(service.getTotalPerMonth());
   }

   @GetMapping("/memberships")
   public ResponseEntity<List<TotalPerMembershipTypeDto>> getTotalPerMembershipType() {
      return ResponseEntity.ok(service.getTotalPerMembershipType());
   }
}
