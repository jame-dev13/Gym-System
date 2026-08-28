package com.jame.dev.gymApp.features.subscription.application.support.updater;

import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionUpdater;
import com.jame.dev.gymApp.features.subscription.application.support.factory.PeriodFactory;
import com.jame.dev.gymApp.features.subscription.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionApplicationUpdater implements SubscriptionUpdater {
   private final PeriodFactory periodFactory;

   @Override
   public void apply(final SubscriptionEntity subscription, final MembershipEntity membershipEntity) {
      final List<PeriodEntity> periods = subscription.getSubscriptionPeriods();
      final PeriodEntity newPeriod = periodFactory.createPeriodFrom(membershipEntity);

      periods.addLast(newPeriod);

      subscription.setMembership(membershipEntity);
      subscription.setSubscriptionPeriods(periods);
      subscription.setStatus(subscription.getStatus());
   }

   @Override
   public void applyRenew(final SubscriptionEntity subscriptionEntity, MembershipEntity newMembership) {
      final PeriodEntity period = periodFactory.createPeriodFrom(newMembership);
      final List<PeriodEntity> periods = periodFactory.createNewPeriodsFrom(subscriptionEntity, period);

      subscriptionEntity.setMembership(newMembership);
      subscriptionEntity.setSubscriptionPeriods(periods);
      subscriptionEntity.setStatus(SubscriptionStatus.ON_RENEWAL);
   }
}
