package com.jame.dev.gymApp.features.subscription.application.contract;

import com.jame.dev.gymApp.application.contract.Updatable;
import com.jame.dev.gymApp.features.subscription.domain.model.PricingEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;

public interface SubscriptionUpdater extends
        Updatable<SubscriptionEntity, PricingEntity> {
   void applyRenew(final SubscriptionEntity subscriptionEntity, final PricingEntity pricingEntity);
}
