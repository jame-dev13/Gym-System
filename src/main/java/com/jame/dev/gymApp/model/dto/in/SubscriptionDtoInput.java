package com.jame.dev.gymApp.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.aspects.annotations.EmailValid;
import com.jame.dev.gymApp.aspects.annotations.NotNullObject;
import com.jame.dev.gymApp.shared.enums.Membership;

public record SubscriptionDtoInput(
        @JsonProperty("customerEmail")
        @EmailValid
        String customerEmail,
        @JsonProperty("membership")
        @NotNullObject Membership membership
) {
}
