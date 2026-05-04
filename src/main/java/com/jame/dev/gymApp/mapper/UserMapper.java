package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.model.dto.out.UserMinimalInfo;
import org.mapstruct.Builder;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring",
        uses = {RoleMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        builder = @Builder)
public interface UserMapper extends BaseMapper<UserEntity, UserDtoOutput> {
   @Override
   UserDtoOutput toDto(UserEntity user);

   UserMinimalInfo toMinimalInfo(UserEntity user);

   default UserEntity toEntity(UserDtoInput dto, Set<RoleEntity> roles) {
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
