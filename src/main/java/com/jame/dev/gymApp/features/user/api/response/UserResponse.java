package com.jame.dev.gymApp.features.user.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.auth.application.model.AuthProvider;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

import java.util.Set;

@Builder
public record UserResponse(
   @JsonProperty("id") Long id,
   @JsonProperty("name") String name,
   @JsonProperty("email") String email,
   @JsonProperty("authProvider") AuthProvider authProvider,
   @JsonProperty("role") Set<Role> roles,
   @JsonProperty("isCustomer") boolean isCustomer,
   @JsonProperty("customerId") @Nullable Long customerId
) {
}
