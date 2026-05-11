package com.jame.dev.gymApp.features.customer.application.support.mapper;

import com.jame.dev.gymApp.features.user.application.support.mapper.UserMapper;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.application.support.mapper.BaseMapper;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CustomerMapper extends BaseMapper<CustomerEntity, CustomerResponse> {

   @Override
   @Mapping(target = "contact", source = "phoneContact")
   CustomerResponse toDto(CustomerEntity entity);

   default CustomerEntity toEntity(CustomerRequest dto, UserEntity user) {
      return CustomerEntity.builder()
         .user(user)
         .phoneContact(dto.contact())
         .build();
   }
}
