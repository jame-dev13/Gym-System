package com.jame.dev.gymApp.features.audit.domain.model;

import com.jame.dev.gymApp.features.auth.application.model.AuthProvider;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public record ChangesModelUser(
   @Nullable Long id,
   String name,
   String email,
   @Nullable AuthProvider authProvider,
   Set<Role> roles
) {
}
