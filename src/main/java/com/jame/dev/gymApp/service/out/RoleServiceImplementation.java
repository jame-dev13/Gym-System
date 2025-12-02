package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.repository.RoleRepository;
import com.jame.dev.gymApp.service.in.RoleService;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImplementation implements RoleService {

   private final RoleRepository repo;

   @Override
   public Optional<RoleEntity> getRoleByRole(@NonNull Role role) {
      return repo.findByRole(role);
   }
}
