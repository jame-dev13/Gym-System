package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.aspects.annotations.aspects.CacheEvictCustomers;
import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.factories.in.CustomerFactory;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.model.dto.in.CustomerFactoryDtoInput;
import com.jame.dev.gymApp.model.dto.in.RecoveryRequest;
import com.jame.dev.gymApp.model.dto.out.CustomerDtoOutput;
import com.jame.dev.gymApp.model.dto.out.PageDto;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.service.in.CustomerRecoverService;
import com.jame.dev.gymApp.service.in.CustomerService;
import com.jame.dev.gymApp.shared.enums.CacheValues;
import com.jame.dev.gymApp.specifications.CustomerSpecification;
import com.jame.dev.gymApp.updaters.in.CustomerUpdater;
import com.jame.dev.gymApp.validators.CustomerValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

import static com.jame.dev.gymApp.shared.enums.CacheValues.CUSTOMERS;

@Service
@RequiredArgsConstructor
@Validated
public class CustomerServiceImplementation implements CustomerService, CustomerRecoverService {
   private final CustomerRepository repo;
   private final CustomerValidator customerValidator;
   private final CustomerFactory customerFactory;
   private final CustomerUpdater customerUpdater;

   @Override
   @Transactional(readOnly = true)
   @Cacheable(
      value = CUSTOMERS,
      keyGenerator = "pageKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public PageDto<CustomerDtoOutput> getPage(Pageable pageable, String search) {
      final Specification<CustomerEntity> spec = new CustomerSpecification(search);
      final Page<CustomerEntity> page = repo.findAll(spec, pageable);
      return customerFactory.createPageFrom(page);
   }

   @Override
   @Transactional(readOnly = true)
   @Cacheable(value = CacheValues.CUSTOMER, key = "#id")
   public CustomerDtoOutput getById(long id) {
      return repo.findById(id)
         .map(customerFactory::createFromEntity)
         .orElseThrow(() -> new CustomerNotFoundException("Customer Not found."));
   }

   @Override
   @Transactional(readOnly = true)
   public Optional<CustomerEntity> getByEmail(String email) {
      return repo.findByUser_Email(email);
   }

   @Override
   @Transactional(readOnly = true)
   public Optional<CustomerEntity> getUserByEmail(final String email) {
      return repo.findByUser_Email(email);
   }

   @Override
   @Transactional(readOnly = true)
   public boolean exitsByIdAndCustomerEmail(long id, String email) {
      return repo.existsByIdAndUser_EmailAndActiveTrue(id, email);
   }

   @Override
   @Transactional
   @CacheEvictCustomers
   public CustomerDtoOutput update(long id, CustomerDtoInput dto) {
      final CustomerEntity customer = repo.findById(id)
         .orElseThrow(() -> new CustomerNotFoundException("Customer not found, id: " + id));
      customerUpdater.apply(customer, dto);
      final CustomerEntity customerSaved = repo.saveAndFlush(customer);
      return customerFactory.createFromEntity(customerSaved);
   }

   @Override
   @Transactional
   @CacheEvict(value = CUSTOMERS, allEntries = true)
   public CustomerDtoOutput save(@NonNull CustomerDtoInput dto) {
      final UserEntity user = customerValidator.validateUserBeforeCreation(dto);
      final CustomerEntity customerEntity = customerFactory
         .createFromInput(new CustomerFactoryDtoInput(user, dto));

      final CustomerEntity customerSaved = repo.saveAndFlush(customerEntity);
      return customerFactory.createFromEntity(customerSaved);
   }

   @Override
   @Transactional
   @CacheEvictCustomers
   public void softDelete(long id) {
      repo.deleteById(id);
   }

   @Override
   @Transactional
   @CacheEvict(value = CUSTOMERS, allEntries = true)
   public void recover(RecoveryRequest recoveryRequest) {
      repo.recoverCustomer(recoveryRequest.email());
   }
}
