package com.jame.dev.gymApp.model.messages;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EmailDetailsWAttachment (
        @NotBlank String recipient,
        @NotBlank String msgBody,
        @NotBlank String subject,
        @NotBlank String attachment
){
}
