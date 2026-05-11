package com.jame.dev.gymApp.features.user.application.support.factory;

import com.jame.dev.gymApp.features.user.domain.model.RoleEntity;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.application.support.factories.PageDtoFactory;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.application.support.mapper.RoleMapper;
import com.jame.dev.gymApp.features.user.application.support.mapper.UserMapper;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.api.response.UserMinimalInfoResponse;
import com.jame.dev.gymApp.features.user.domain.repository.RoleRepository;
import com.jame.dev.gymApp.features.auth.application.model.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserApplicationFactory implements UserFactory {
   private final RoleMapper roleMapper;
   private final RoleRepository roleRepository;
   private final UserMapper userMapper;
   private final PasswordEncoder passwordEncoder;
   private final PageDtoFactory<UserEntity, UserResponse> pageUserFactory;

   @Override
   public PageDto<UserResponse> createPageFrom(Page<UserEntity> page) {
      return pageUserFactory.createPageDtoFrom(page);
   }

   @Override
   public UserResponse createFromEntity(UserEntity entity) {
      return userMapper.toDto(entity);
   }

   @Override
   public UserEntity createFromInput(UserRequest dto) {
      final Set<RoleEntity> roles = roleMapper.toEntitySet(dto.roles(), roleRepository);
      final UserEntity userEntity = userMapper.toEntity(dto, roles);
      if (userEntity.getProvider() == AuthProvider.LOCAL)
         userEntity.setPassword(passwordEncoder.encode(dto.password()));
      userEntity.setCreatedAt(Instant.now());
      return userEntity;
   }

   @Override
   public PageDto<UserMinimalInfoResponse> createMinimalInfoPage(Page<UserMinimalInfoResponse> page) {
      return new PageDto<>(
         page.getContent(),
         page.getNumber(),
         page.getSize(),
         page.getTotalElements(),
         page.getSort().toString(),
         page.getSort().isSorted() ? "ASC" : "DESC"
      );
   }
}
