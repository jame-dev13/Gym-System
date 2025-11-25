package com.jame.dev.gymApp.controller.advice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record ApiErrorResponse(
        @JsonProperty("timestamp") ZonedDateTime timestamp,
        @JsonProperty("status") int status,
        @JsonProperty("error") String error,
        @JsonProperty("message") String message,
        @JsonProperty("path") String path,
        @JsonProperty("code") String code
) {}

