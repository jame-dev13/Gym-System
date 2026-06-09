package com.jame.dev.gymApp.features.user.application.support.updater;

import com.jame.dev.gymApp.features.auth.application.model.AuthProvider;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.application.contract.UserUpdater;
import com.jame.dev.gymApp.features.user.application.support.mapper.RoleMapper;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.infrastructure.persistence.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserApplicationUpdater implements UserUpdater {
   private final RoleMapper roleMapper;
   private final RoleRepository roleRepository;
   private final PasswordEncoder passwordEncoder;

   @Override
   public void apply(final UserEntity userEntity, final UserRequest dto) {
      final boolean pwdCondition = dto.password() == null || dto.password().isBlank();
      final String oldPassword = userEntity.getPassword();
      final String passwordFinal = (pwdCondition) ? oldPassword : passwordEncoder.encode(dto.password());

      userEntity.setName(dto.name());
      userEntity.setEmail(dto.email());
      userEntity.setPassword(passwordFinal);
      userEntity.setProvider(AuthProvider.LOCAL != dto.authProvider() ? AuthProvider.LOCAL : dto.authProvider());
      userEntity.setRoles(roleMapper.toEntitySet(dto.roles(), roleRepository));
   }
}
