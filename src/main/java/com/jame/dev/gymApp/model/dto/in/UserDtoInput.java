package com.jame.dev.gymApp.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.shared.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Builder
public record UserDtoInput(
        @JsonProperty("name") @NotBlank String name,
        @JsonProperty("email") @NotBlank @Email String email,
        @JsonProperty("password") @NotBlank String password,
        @JsonProperty("role") @NotNull Role role,
        @JsonProperty("active") @NotNull Boolean active
        ) {
   public UserDtoInput{
      password = new BCryptPasswordEncoder().encode(password);
      active = Boolean.TRUE;
   }
}
