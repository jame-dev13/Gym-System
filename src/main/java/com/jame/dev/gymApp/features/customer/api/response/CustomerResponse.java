package com.jame.dev.gymApp.features.customer.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import lombok.Builder;

@Builder
public record CustomerResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("user") UserResponse user,
        @JsonProperty("contact") String contact
) {
}
