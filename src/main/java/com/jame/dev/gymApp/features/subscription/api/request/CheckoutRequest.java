package com.jame.dev.gymApp.features.subscription.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import jakarta.validation.constraints.Email;

public record CheckoutRequest(
    @JsonProperty("membership")
    @NotNullObject
    Membership membership,
    @JsonProperty("customerEmail")
    @Email
    String customerEmail
) {
}
