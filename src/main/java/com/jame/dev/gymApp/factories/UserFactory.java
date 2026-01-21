package com.jame.dev.gymApp.factories;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.mapper.RoleMapper;
import com.jame.dev.gymApp.mapper.UserMapper;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.repository.RoleRepository;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserFactory {
   private final RoleMapper roleMapper;
   private final RoleRepository roleRepository;
   private final UserMapper userMapper;
   private final PasswordEncoder passwordEncoder;

   public @NonNull UserEntity createFrom(@NonNull UserDtoInput dto) {
      final Set<RoleEntity> roles = roleMapper.toEntitySet(dto.roles(), roleRepository);
      final UserEntity userEntity = userMapper.toEntity(dto, roles);
      if(userEntity.getProvider() == AuthProvider.LOCAL)
         userEntity.setPassword(passwordEncoder.encode(dto.password()));
      return userEntity;
   }
}
