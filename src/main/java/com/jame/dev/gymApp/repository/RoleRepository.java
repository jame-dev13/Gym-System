package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<@NonNull RoleEntity, @NonNull Integer> {
   Optional<RoleEntity> findByRole(@NonNull final Role role);
}
