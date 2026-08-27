package com.jame.dev.gymApp.features.subscription.application.contract;

import com.jame.dev.gymApp.application.contract.Updatable;
import com.jame.dev.gymApp.features.subscription.domain.model.MembershipEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;

public interface SubscriptionUpdater extends
        Updatable<SubscriptionEntity, MembershipEntity> {
   void applyRenew(final SubscriptionEntity subscriptionEntity, final MembershipEntity membershipEntity);
}
