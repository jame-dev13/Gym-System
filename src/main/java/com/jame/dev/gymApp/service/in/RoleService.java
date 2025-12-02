package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.NonNull;

import java.util.Optional;

public interface RoleService {
   Optional<RoleEntity> getRoleByRole(@NonNull final Role role);
}
