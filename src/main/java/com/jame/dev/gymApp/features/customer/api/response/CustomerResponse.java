package com.jame.dev.gymApp.features.customer.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record CustomerResponse(
        @JsonProperty("id") long id,
        @JsonProperty("customerName") String customerName,
        @JsonProperty("customerEmail") String customerEmail,
        @JsonProperty("contact") String contact,
        @JsonProperty("isSubscriber") boolean isSubscriber,
        @JsonProperty("subscriptionId") @Nullable Long subscriptionId
) {
}
