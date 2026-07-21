package com.jame.dev.gymApp.features.audit.domain.model;

import com.jame.dev.gymApp.features.subscription.application.dto.PeriodResponse;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionStatus;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ChangesModelSubscription(
   @Nullable Long id,
   String customerEmail,
   @Nullable BigDecimal price,
   Membership membership,
   @Nullable List<PeriodResponse> periods,
   @Nullable SubscriptionStatus status
) {
}
