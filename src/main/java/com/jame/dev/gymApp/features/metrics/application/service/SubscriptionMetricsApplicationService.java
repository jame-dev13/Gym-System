package com.jame.dev.gymApp.features.metrics.application.service;

import com.jame.dev.gymApp.features.metrics.domain.repository.SubscriptionMetricsRepository;
import com.jame.dev.gymApp.features.metrics.application.contract.SubscriptionMetricsService;
import com.jame.dev.gymApp.features.metrics.domain.model.SubsPerMembership;
import com.jame.dev.gymApp.features.metrics.domain.model.SubsPerMonthDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionMetricsApplicationService implements SubscriptionMetricsService {
   private final SubscriptionMetricsRepository repo;

   @Override
   public long getTotalSubscriptions() {
      return repo.countDistinctByActiveTrueAndFinishedFalse();
   }

   @Override
   public long getSubscriptionsBefore(@NonNull LocalDate date) {
      return repo.countByStartDateBefore(date);
   }


   @Override
   public List<SubsPerMonthDto> getSubscriptionsPerMonth() {
      return returnList(repo.countSubsByMonth());
   }

   @Override
   public List<SubsPerMembership> getSubscriptionsPerMembership() {
       return returnList(repo.countSubsByMembership());
   }

   private <T> List<T> returnList(List<T> list) {
      return (list.isEmpty()) ? List.of() : list;
   }
}
