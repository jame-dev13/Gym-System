package com.jame.dev.gymApp.features.notification.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EmailDetails(
        @NotBlank String recipient,
        @NotBlank String msgBody,
        @NotBlank String subject
) {
}
