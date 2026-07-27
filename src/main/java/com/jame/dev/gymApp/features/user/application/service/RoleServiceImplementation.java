package com.jame.dev.gymApp.features.user.application.service;

import com.jame.dev.gymApp.features.user.domain.model.RoleEntity;
import com.jame.dev.gymApp.features.user.infrastructure.persistence.RoleRepository;
import com.jame.dev.gymApp.features.user.application.contract.RoleService;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
public class RoleServiceImplementation implements RoleService {

   private final RoleRepository repo;

   @Override
   public Optional<RoleEntity> getRoleByRole(@NonNull Role role) {
      return repo.findByRole(role);
   }
}
