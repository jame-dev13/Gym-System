package com.jame.dev.gymApp.updaters;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.mapper.RoleMapper;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.repository.RoleRepository;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUpdater {
   private final RoleMapper roleMapper;
   private final RoleRepository roleRepository;
   private final PasswordEncoder passwordEncoder;

   public void apply(final UserEntity userEntity, final UserDtoInput dto) {
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
