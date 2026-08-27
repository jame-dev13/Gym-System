package com.jame.dev.gymApp.features.subscription.application.support.factory;

import com.jame.dev.gymApp.features.subscription.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PeriodFactory {

   public PeriodEntity createPeriodFrom(final MembershipEntity membership) {
      final String valueName = membership.getMembership().name();
      final Optional<Period> periodOptional = Optional.of(Period.valueOf(valueName));
      final Period period = periodOptional
         .orElseThrow(() -> new IllegalArgumentException("No period value present for: " + valueName));
      return new PeriodEntity(period);
   }

   public List<PeriodEntity> createPeriodsFrom(
      final MembershipEntity membership) {
      return List.of(createPeriodFrom(membership));
   }

   public List<PeriodEntity> createNewPeriodsFrom(
      final SubscriptionEntity subscriptionEntity,
      final PeriodEntity periodEntity) {
      final var periodList = subscriptionEntity.getSubscriptionPeriods();
      periodList.addLast(periodEntity);
      return periodList;
   }

   public LocalDate createNewStartDateFrom(final LocalDate endPeriod) {
      final LocalDate now = LocalDate.now();
      final long daysDiff = ChronoUnit.DAYS.between(endPeriod, now);
      return (daysDiff <= 0) ? now : now.plusDays(daysDiff);
   }
}
