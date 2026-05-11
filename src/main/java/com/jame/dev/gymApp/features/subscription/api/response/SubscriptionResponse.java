package com.jame.dev.gymApp.features.subscription.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.subscription.application.dto.PeriodDtoOutput;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record SubscriptionResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("customer") CustomerResponse customer,
        @JsonProperty("membership") Membership membership,
        @JsonProperty("price") BigDecimal price,
        @JsonProperty("periods") List<PeriodDtoOutput> periods,
        @JsonProperty("finished") Boolean finished
) {
}
