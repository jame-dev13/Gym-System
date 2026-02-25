package com.jame.dev.gymApp.config.app;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.factories.PageDtoFactory;
import com.jame.dev.gymApp.mapper.CustomerMapper;
import com.jame.dev.gymApp.mapper.SubscriptionMapper;
import com.jame.dev.gymApp.mapper.UserMapper;
import com.jame.dev.gymApp.model.dto.out.CustomerDtoOutput;
import com.jame.dev.gymApp.model.dto.out.SubscriptionDtoOutput;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FactoryConfig {

   @Bean
   public PageDtoFactory<UserEntity, UserDtoOutput> pageUserFactory(final UserMapper userMapper) {
      return new PageDtoFactory<>(userMapper);
   }

   @Bean
   public PageDtoFactory<CustomerEntity, CustomerDtoOutput> pageCustomerFactory(final CustomerMapper customerMapper) {
      return new PageDtoFactory<>(customerMapper);
   }

   @Bean
   public PageDtoFactory<SubscriptionEntity, SubscriptionDtoOutput> pageSubscriptionFactory(final SubscriptionMapper subscriptionMapper) {
      return new PageDtoFactory<>(subscriptionMapper);
   }
}
