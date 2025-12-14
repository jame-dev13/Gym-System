package com.jame.dev.gymApp.controller.advice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record ApiErrorResponse(
        @JsonProperty("timestamp") OffsetDateTime timestamp,
        @JsonProperty("status") int status,
        @JsonProperty("error") String error,
        @JsonProperty("message") String message,
        @JsonProperty("path") String path,
        @JsonProperty("code") String code
) {}

