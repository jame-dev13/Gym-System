package com.jame.dev.gymApp.features.customer.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import org.jspecify.annotations.Nullable;

public record CustomerRequest(
        @JsonProperty("userEmail")
        @EmailValid String email,
        @JsonProperty("contact")
        @Nullable
        String contact
) {
}
