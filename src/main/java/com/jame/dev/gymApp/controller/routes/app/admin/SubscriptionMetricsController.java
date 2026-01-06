package com.jame.dev.gymApp.controller.routes.app.admin;

import com.jame.dev.gymApp.metrics.service.in.SubscriptionMetricsService;
import com.jame.dev.gymApp.model.metrics.PeriodCountDto;
import com.jame.dev.gymApp.model.metrics.SubsPerMembership;
import com.jame.dev.gymApp.model.metrics.SubsPerMonthDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app/v1/administration/metrics/subs")
@RequiredArgsConstructor
public class SubscriptionMetricsController {

   private final SubscriptionMetricsService service;

   @GetMapping("/total")
   public ResponseEntity<Map<String, Long>> getTotalSubscriptions(){
      final long total = service.getTotalSubscriptions();
      return buildResponseMap(total);
   }

   @GetMapping("/before/{date}")
   public ResponseEntity<Map<String, Long>> getTotalBefore(@PathVariable("date") final LocalDate date){
      final long total = service.getSubscriptionsBefore(date);
      return buildResponseMap(total);
   }

   @GetMapping("/periods")
   public ResponseEntity<List<PeriodCountDto>> getPerPeriod(){
      return ResponseEntity.ok(service.getSubscriptionsPerPeriod());
   }

   @GetMapping("/memberships")
   public ResponseEntity<List<SubsPerMembership>> getSubsPerMembership() {
      return ResponseEntity.ok(service.getSubscriptionsPerMembership());
   }

   @GetMapping("/months")
   public ResponseEntity<List<SubsPerMonthDto>> getSubsPerMonth() {
      return ResponseEntity.ok(service.getSubscriptionsPerMonth());
   }

   private ResponseEntity<Map<String, Long>> buildResponseMap(long total){
      return ResponseEntity.ok()
              .contentType(MediaType.APPLICATION_JSON)
              .body(Map.of("total", total));
   }
}
