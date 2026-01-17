package com.jame.dev.gymApp.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.shared.enums.Membership;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubscriptionDtoInput(
        @JsonProperty("customerEmail")
        @NotNull
        @NotBlank
        @Email String customerEmail,
        @JsonProperty("membership")
        @NotNull Membership membership
) {
}
