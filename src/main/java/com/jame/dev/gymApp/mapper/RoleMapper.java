package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.shared.enums.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
   default RoleEntity toEntity(Role role){
      if(role == null) return null;
      RoleEntity roleEntity = new RoleEntity();
      roleEntity.setRole(role);
      return roleEntity;
   }

   default Role toDto(RoleEntity entity){
      if(entity == null) return null;
      return entity.getRole();
   }
}
