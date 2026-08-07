package com.jame.dev.gymApp.features.audit.application.model;

import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SubscriptionBeforeUpdateModel(
   Membership membership,
   BigDecimal price,
   SubscriptionStatus status
) {
}
