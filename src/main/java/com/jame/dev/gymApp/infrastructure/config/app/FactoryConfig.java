package com.jame.dev.gymApp.infrastructure.config.app;

import com.jame.dev.gymApp.application.support.factories.PageDtoFactory;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.support.mapper.CustomerMapper;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.support.mapper.SubscriptionMapper;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.application.support.mapper.UserMapper;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FactoryConfig {

   @Bean
   public PageDtoFactory<UserEntity, UserResponse> pageUserFactory(final UserMapper userMapper) {
      return new PageDtoFactory<>(userMapper);
   }

   @Bean
   public PageDtoFactory<CustomerEntity, CustomerResponse> pageCustomerFactory(final CustomerMapper customerMapper) {
      return new PageDtoFactory<>(customerMapper);
   }

   @Bean
   public PageDtoFactory<SubscriptionEntity, SubscriptionResponse> pageSubscriptionFactory(final SubscriptionMapper subscriptionMapper) {
      return new PageDtoFactory<>(subscriptionMapper);
   }
}
