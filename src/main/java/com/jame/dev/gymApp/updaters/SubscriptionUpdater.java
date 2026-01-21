package com.jame.dev.gymApp.updaters;

import com.jame.dev.gymApp.entity.PeriodEntity;
import com.jame.dev.gymApp.entity.PricingEntity;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.factories.PeriodFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionUpdater {
   private final PeriodFactory periodFactory;

   public SubscriptionEntity apply(
           final SubscriptionEntity subscriptionEntity, PricingEntity pricing, final LocalDate endPeriod) {
      final LocalDate now = LocalDate.now();
      final long daysDiff = ChronoUnit.DAYS.between(endPeriod, now);
      final LocalDate startDate = (daysDiff <= 0) ? now : now.plusDays(daysDiff);
      final PeriodEntity period = periodFactory.createPeriodFrom(pricing, startDate);
      final List<PeriodEntity> periods = subscriptionEntity.getSubscriptionPeriods();
      periods.add(period);

      subscriptionEntity.setPricing(pricing);
      subscriptionEntity.setSubscriptionPeriods(periods);
      subscriptionEntity.setFinished(false);

      return subscriptionEntity;
   }

}
