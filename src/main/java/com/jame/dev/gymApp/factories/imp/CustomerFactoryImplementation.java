package com.jame.dev.gymApp.factories.imp;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.factories.PageDtoFactory;
import com.jame.dev.gymApp.factories.in.CustomerFactory;
import com.jame.dev.gymApp.mapper.CustomerMapper;
import com.jame.dev.gymApp.model.dto.in.CustomerFactoryDtoInput;
import com.jame.dev.gymApp.model.dto.out.CustomerDtoOutput;
import com.jame.dev.gymApp.model.dto.out.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CustomerFactoryImplementation implements CustomerFactory {
   private final CustomerMapper customerMapper;
   private final PageDtoFactory<CustomerEntity, CustomerDtoOutput> pageCustomerFactory;

   @Override
   public PageDto<CustomerDtoOutput> createPageFrom(Page<CustomerEntity> page) {
      return pageCustomerFactory.createPageDtoFrom(page);
   }

   @Override
   public CustomerDtoOutput createFromEntity(CustomerEntity entity) {
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
