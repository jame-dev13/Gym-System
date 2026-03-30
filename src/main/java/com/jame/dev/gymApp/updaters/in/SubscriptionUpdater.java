package com.jame.dev.gymApp.updaters.in;

import com.jame.dev.gymApp.entity.PricingEntity;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.updaters.common.Renewable;
import com.jame.dev.gymApp.updaters.common.Updatable;

public interface SubscriptionUpdater extends
        Updatable<SubscriptionEntity, PricingEntity>, Renewable<SubscriptionEntity, PricingEntity> {
}
