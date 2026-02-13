package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.model.dto.out.CustomerDtoOutput;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CustomerMapper extends BaseMapper<CustomerEntity, CustomerDtoOutput>{

   @Override
   @Mapping(target = "contact", source = "phoneContact")
   CustomerDtoOutput toDto(CustomerEntity entity);

   default CustomerEntity toEntity(CustomerDtoInput dto, UserEntity user){
      return CustomerEntity.builder()
              .user(user)
              .phoneContact(dto.contact())
              .build();
   }
}
