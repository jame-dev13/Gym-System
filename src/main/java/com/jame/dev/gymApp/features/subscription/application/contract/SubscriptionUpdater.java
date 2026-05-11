package com.jame.dev.gymApp.features.subscription.application.contract;

import com.jame.dev.gymApp.features.subscription.domain.model.PricingEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.application.contract.Renewable;
import com.jame.dev.gymApp.application.contract.Updatable;

public interface SubscriptionUpdater extends
        Updatable<SubscriptionEntity, PricingEntity>, Renewable<SubscriptionEntity, PricingEntity> {
}
