package com.jame.dev.gymApp.features.customer.application.support.factory;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.application.support.factories.PageDtoFactory;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.dto.CustomerFactoryDtoInput;
import com.jame.dev.gymApp.features.customer.application.support.mapper.CustomerMapper;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CustomerApplicationFactory implements CustomerFactory {
   private final CustomerMapper customerMapper;
   private final PageDtoFactory<CustomerEntity, CustomerResponse> pageCustomerFactory;

   @Override
   public PageDto<CustomerResponse> createPageFrom(Page<CustomerEntity> page) {
      return pageCustomerFactory.createPageDtoFrom(page);
   }

   @Override
   public CustomerResponse createFromEntity(CustomerEntity entity) {
      return customerMapper.toDto(entity);
   }

   @Override
   public CustomerEntity createFromInput(CustomerFactoryDtoInput input) {
      final CustomerEntity customerEntity = customerMapper.toEntity(
              input.dto(), input.userEntity()
      );
      customerEntity.setCreatedAt(Instant.now());
      return customerEntity;
   }
}
