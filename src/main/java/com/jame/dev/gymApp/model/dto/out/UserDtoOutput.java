package com.jame.dev.gymApp.model.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@JsonSerialize
public record UserDtoOutput(
        @JsonProperty("name") @NotBlank String name,
        @JsonProperty("email") @NotBlank @Email  String email
) {
}
