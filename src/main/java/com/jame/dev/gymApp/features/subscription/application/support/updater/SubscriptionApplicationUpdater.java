package com.jame.dev.gymApp.features.subscription.application.support.updater;

import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionUpdater;
import com.jame.dev.gymApp.features.subscription.application.support.factory.PeriodFactory;
import com.jame.dev.gymApp.features.subscription.domain.model.PeriodEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.PricingEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionApplicationUpdater implements SubscriptionUpdater {
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
      subscription.setStatus(subscription.getStatus());
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
      subscriptionEntity.setStatus(SubscriptionStatus.NOT_PAID);
   }
}
