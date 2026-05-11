package com.jame.dev.gymApp.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PageDto<T> (
        @JsonProperty("content") List<T> content,
        @JsonProperty("page") int page,
        @JsonProperty("size") int size,
        @JsonProperty("totalElements") long totalElements,
        @JsonProperty("sortProperty") String sortProperty,
        @JsonProperty("sortDirection") String sortDirection
) {
}
