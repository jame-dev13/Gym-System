package com.jame.dev.gymApp.factories;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.factories.in.Factory;
import com.jame.dev.gymApp.mapper.RoleMapper;
import com.jame.dev.gymApp.mapper.UserMapper;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.PageDto;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.repository.RoleRepository;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserFactory implements Factory<UserEntity, UserDtoOutput, UserDtoInput> {
   private final RoleMapper roleMapper;
   private final RoleRepository roleRepository;
   private final UserMapper userMapper;
   private final PasswordEncoder passwordEncoder;
   private final PageDtoFactory<UserEntity, UserDtoOutput> pageUserFactory;

   @Override
   public PageDto<UserDtoOutput> createPageFrom(Page<UserEntity> page) {
      return pageUserFactory.createPageDtoFrom(page);
   }

   @Override
   public UserDtoOutput createFromEntity(UserEntity entity) {
      return userMapper.toDto(entity);
   }

   @Override
   public UserEntity createFromInput(UserDtoInput dto) {
      final Set<RoleEntity> roles = roleMapper.toEntitySet(dto.roles(), roleRepository);
      final UserEntity userEntity = userMapper.toEntity(dto, roles);
      if (userEntity.getProvider() == AuthProvider.LOCAL)
         userEntity.setPassword(passwordEncoder.encode(dto.password()));
      userEntity.setCreatedAt(Instant.now());
      return userEntity;
   }
}
