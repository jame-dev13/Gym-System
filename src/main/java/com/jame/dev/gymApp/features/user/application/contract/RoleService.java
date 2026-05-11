package com.jame.dev.gymApp.features.user.application.contract;

import com.jame.dev.gymApp.features.user.domain.model.RoleEntity;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import lombok.NonNull;

import java.util.Optional;

public interface RoleService {
   Optional<RoleEntity> getRoleByRole(@NonNull final Role role);
}
