package com.jame.dev.gymApp.features.user.application.support.mapper;

import com.jame.dev.gymApp.features.user.domain.model.RoleEntity;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.api.response.UserMinimalInfoResponse;
import com.jame.dev.gymApp.application.support.mapper.BaseMapper;
import org.mapstruct.Builder;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring",
        uses = {RoleMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        builder = @Builder)
public interface UserMapper extends BaseMapper<UserEntity, UserResponse> {
   @Override
   UserResponse toDto(UserEntity user);

   UserMinimalInfoResponse toMinimalInfo(UserEntity user);

   default UserEntity toEntity(UserRequest dto, Set<RoleEntity> roles) {
      if (dto == null) return null;
      return UserEntity.builder()
              .name(dto.name())
              .email(dto.email())
              .password(dto.password())
              .provider(dto.authProvider())
              .roles(roles)
              .build();
   }
}
