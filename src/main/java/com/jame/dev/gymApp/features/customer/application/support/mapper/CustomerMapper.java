package com.jame.dev.gymApp.features.customer.application.support.mapper;

import com.jame.dev.gymApp.application.support.mapper.BaseMapper;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.user.application.support.mapper.UserMapper;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import org.jspecify.annotations.Nullable;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Mapper(componentModel = "spring", uses = UserMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CustomerMapper extends BaseMapper<CustomerEntity, CustomerResponse> {

   @Override
   @Mapping(target = "customerName", source = "entity.user.name")
   @Mapping(target = "customerEmail", source = "entity.user.email")
   @Mapping(target = "contact", source = "phoneContact")
   @Mapping(
      target = "isSubscriber",
      expression = "java(mapIsSubscriber(entity))")
   @Mapping(target = "subscriptionId", expression = "java(mapSubscriptionId(entity))")
   CustomerResponse toDto(CustomerEntity entity);

   default boolean mapIsSubscriber(CustomerEntity entity) {
      return Optional.ofNullable(entity.getSubscriptions())
         .filter(Predicate.not(List::isEmpty))
         .map(List::getLast)
         .map(SubscriptionEntity::isActive)
         .orElse(false);
   }

   default @Nullable Long mapSubscriptionId(CustomerEntity entity) {
      return Optional.ofNullable(entity)
         .map(CustomerEntity::getSubscriptions)
         .filter(Predicate.not(List::isEmpty))
         .map(List::getLast)
         .filter(SubscriptionEntity::isActive)
         .map(SubscriptionEntity::getId)
         .orElse(null);
   }

   default CustomerEntity toEntity(CustomerRequest dto, UserEntity user) {
      return CustomerEntity.builder()
         .user(user)
         .phoneContact(dto.contact())
         .build();
   }
}
