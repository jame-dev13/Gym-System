package com.jame.dev.gymApp.features.user.infrastructure.persistence;

import com.jame.dev.gymApp.features.user.domain.model.RoleEntity;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<@NonNull RoleEntity, @NonNull Integer> {
   Optional<RoleEntity> findByRole(@NonNull final Role role);
   RoleEntity getReferenceByRole(@NonNull final Role role);
}
