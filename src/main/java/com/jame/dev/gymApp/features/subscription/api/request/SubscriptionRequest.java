package com.jame.dev.gymApp.features.subscription.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;

public record SubscriptionRequest(
        @JsonProperty("customerEmail")
        @EmailValid
        String customerEmail,
        @JsonProperty("membership")
        @NotNullObject Membership membership
) {
}
