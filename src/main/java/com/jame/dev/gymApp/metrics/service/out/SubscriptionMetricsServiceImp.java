package com.jame.dev.gymApp.metrics.service.out;

import com.jame.dev.gymApp.metrics.repo.SubscriptionMetricsRepository;
import com.jame.dev.gymApp.metrics.service.in.SubscriptionMetricsService;
import com.jame.dev.gymApp.model.metrics.SubsPerMembership;
import com.jame.dev.gymApp.model.metrics.SubsPerMonthDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionMetricsServiceImp implements SubscriptionMetricsService {
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
