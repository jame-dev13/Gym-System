package com.jame.dev.gymApp.model.messages;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EmailDetails(
        @NotBlank String recipient,
        @NotBlank String msgBody,
        @NotBlank String subject
) {
}
