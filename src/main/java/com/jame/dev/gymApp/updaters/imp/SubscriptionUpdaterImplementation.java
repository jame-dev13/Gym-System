package com.jame.dev.gymApp.updaters.imp;

import com.jame.dev.gymApp.entity.PeriodEntity;
import com.jame.dev.gymApp.entity.PricingEntity;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.factories.PeriodFactory;
import com.jame.dev.gymApp.updaters.in.SubscriptionUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionUpdaterImplementation implements SubscriptionUpdater {
   private final PeriodFactory periodFactory;

   @Override
   public void apply(
           final SubscriptionEntity subscription,
           final PricingEntity newPricing) {
      final LocalDate now = LocalDate.now();
      final List<PeriodEntity> periods = subscription.getSubscriptionPeriods();
      final PeriodEntity newPeriod = periodFactory.createPeriodFrom(newPricing, now);

      periods.addLast(newPeriod);

      subscription.setPricing(newPricing);
      subscription.setSubscriptionPeriods(periods);
      subscription.setFinished(false);
      subscription.setUpdatedAt(Instant.now());
   }

   @Override
   public void applyRenew(
           final SubscriptionEntity subscriptionEntity,
           PricingEntity pricing) {
      final LocalDate endPeriod = subscriptionEntity.getSubscriptionPeriods().getLast().getEndPeriod();
      final LocalDate startDate = periodFactory.createNewStartDateFrom(endPeriod);
      final PeriodEntity period = periodFactory.createPeriodFrom(pricing, startDate);
      final List<PeriodEntity> periods = periodFactory.createNewPeriodsFrom(subscriptionEntity, period);

      subscriptionEntity.setPricing(pricing);
      subscriptionEntity.setSubscriptionPeriods(periods);
      subscriptionEntity.setFinished(false);
      subscriptionEntity.setUpdatedAt(Instant.now());
   }
}
