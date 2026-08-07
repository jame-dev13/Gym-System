package com.jame.dev.gymApp.features.audit.application.model;

import com.jame.dev.gymApp.features.user.domain.model.Role;
import lombok.Builder;

import java.util.Set;

@Builder
public record UserBeforeUpdateModel(
   String name,
   String email,
   Set<Role> roles
) {
}
