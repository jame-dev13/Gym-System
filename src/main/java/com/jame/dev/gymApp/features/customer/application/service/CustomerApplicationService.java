package com.jame.dev.gymApp.features.customer.application.service;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.customer.infrastructure.annotations.CacheEvictCustomers;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.application.dto.CustomerFactoryDtoInput;
import com.jame.dev.gymApp.features.auth.api.request.RecoveryRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerRepository;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerRecoverService;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerService;
import com.jame.dev.gymApp.application.model.CacheValues;
import com.jame.dev.gymApp.features.customer.infrastructure.specification.CustomerSpecification;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerUpdater;
import com.jame.dev.gymApp.features.customer.application.support.validator.CustomerValidator;
import com.jame.dev.gymApp.infrastructure.sort.SortPropertyResolver;
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

import static com.jame.dev.gymApp.application.model.CacheValues.CUSTOMERS;

@Service
@RequiredArgsConstructor
@Validated
public class CustomerApplicationService implements CustomerService, CustomerRecoverService {
   private final CustomerRepository repo;
   private final CustomerValidator customerValidator;
   private final CustomerFactory customerFactory;
   private final CustomerUpdater customerUpdater;
   private final SortPropertyResolver customerSortAppResolver;

   @Override
   @Transactional(readOnly = true)
   @Cacheable(
      value = CUSTOMERS,
      keyGenerator = "pageKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public PageDto<CustomerResponse> getPage(Pageable pageable, String search) {
      final Pageable pageableWrapped = customerSortAppResolver.resolve(pageable);
      final Specification<CustomerEntity> spec = new CustomerSpecification(search);
      final Page<CustomerEntity> page = repo.findAll(spec, pageableWrapped);
      return customerFactory.createPageFrom(page);
   }

   @Override
   @Transactional(readOnly = true)
   @Cacheable(value = CacheValues.CUSTOMER, key = "#id")
   public CustomerResponse getById(long id) {
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
   @AuditLog(
      action = AuditLogAction.UPDATE,
      entityType = AuditLogEntityType.CUSTOMER,
      entityId = "#id",
      input = "#dto",
      result = "#result"
   )
   public CustomerResponse update(long id, CustomerRequest dto) {
      final CustomerEntity customer = repo.findById(id)
         .orElseThrow(() -> new CustomerNotFoundException("Customer not found, id: " + id));
      customerUpdater.apply(customer, dto);
      final CustomerEntity customerSaved = repo.saveAndFlush(customer);
      return customerFactory.createFromEntity(customerSaved);
   }

   @Override
   @Transactional
   @CacheEvict(value = CUSTOMERS, allEntries = true)
   @AuditLog(
      action = AuditLogAction.INSERT,
      entityType = AuditLogEntityType.CUSTOMER,
      entityId = "#result.id",
      input = "#dto",
      result = "#result"
   )
   public CustomerResponse save(@NonNull CustomerRequest dto) {
      final UserEntity user = customerValidator.validateUserBeforeCreation(dto);
      final CustomerEntity customerEntity = customerFactory
         .createFromInput(new CustomerFactoryDtoInput(user, dto));

      final CustomerEntity customerSaved = repo.saveAndFlush(customerEntity);
      return customerFactory.createFromEntity(customerSaved);
   }

   @Override
   @Transactional
   @CacheEvictCustomers
   @AuditLog(
      action = AuditLogAction.DELETE,
      entityType = AuditLogEntityType.CUSTOMER,
      entityId = "#id"
   )
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
