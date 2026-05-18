package com.jame.dev.gymApp.features.subscription.application.service;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.subscription.infrastructure.annotations.CacheEvictSubscriptions;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.PricingEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.exception.PricingNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.application.dto.SubscriptionFactoryDtoInput;
import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.PricingRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionRepository;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionService;
import com.jame.dev.gymApp.application.model.CacheValues;
import com.jame.dev.gymApp.features.subscription.infrastructure.specification.SubscriptionSpecification;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionUpdater;
import com.jame.dev.gymApp.features.subscription.application.support.validator.SubscriptionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class SubscriptionApplicationService implements SubscriptionService {
   private final SubscriptionRepository repo;
   private final CustomerRepository customerRepo;
   private final PricingRepository pricingRepo;
   private final SubscriptionFactory subscriptionFactory;
   private final SubscriptionUpdater subscriptionUpdater;
   private final SubscriptionValidator validator;

   @Override
   @Transactional(readOnly = true)
   @Cacheable(
      value = CacheValues.SUBSCRIPTIONS,
      keyGenerator = "pageKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public PageDto<SubscriptionResponse> getPage(Pageable pageable, String search) {
      final Specification<SubscriptionEntity> spec = new SubscriptionSpecification(search);
      final Page<SubscriptionEntity> entityPage = repo.findAll(spec, pageable);
      return subscriptionFactory.createPageFrom(entityPage);
   }

   @Override
   @Transactional(readOnly = true)
   @Cacheable(value = CacheValues.SUBSCRIPTION, key = "#id")
   public SubscriptionResponse getById(long id) {
      return repo.findById(id)
         .map(subscriptionFactory::createFromEntity)
         .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not found."));
   }

   @Transactional(readOnly = true)
   @Override
   public Optional<SubscriptionEntity> getByEmail(String email) {
      return repo.findActiveSubscriptionByEmail(email);
   }

   @Override
   @Transactional(readOnly = true)
   public boolean exitsByIdAndCustomerEmail(long id, String email) {
      return repo.existsByIdAndCustomer_User_EmailAndActiveTrue(id, email);
   }

   @Override
   @Transactional
   @CacheEvict(value = CacheValues.SUBSCRIPTIONS, allEntries = true)
   @AuditLog(
      action = AuditLogAction.INSERT,
      entityType = AuditLogEntityType.SUBSCRIPTION,
      input = "#dto",
      entityId = "#result.id",
      result = "#result"
   )
   public SubscriptionResponse save(SubscriptionRequest dto) {
      final CustomerEntity customer = customerRepo.findByUser_Email(dto.customerEmail())
         .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found."));

      if (repo.existsByCustomer(customer)) {
         throw new AlreadyExistsException("There's a subscription linked to the customer with: " + dto.customerEmail());
      }

      final PricingEntity pricing = pricingRepo.findByMemberShipEntity_Membership(dto.membership())
         .orElseThrow(() -> new PricingNotFoundException("Pricing Not Found."));

      final SubscriptionEntity subscriptionEntity = subscriptionFactory.createFromInput(
         new SubscriptionFactoryDtoInput(dto, customer, pricing, LocalDate.now()));

      final SubscriptionEntity subscriptionSaved = repo.saveAndFlush(subscriptionEntity);

      return subscriptionFactory.createFromEntity(subscriptionSaved);
   }

   @Override
   @Transactional
   @CacheEvictSubscriptions
   @AuditLog(
      action = AuditLogAction.UPDATE,
      entityType = AuditLogEntityType.SUBSCRIPTION,
      input = "#dto",
      entityId = "#id",
      result = "#result"
   )
   public SubscriptionResponse update(long id, SubscriptionRequest dto) {
      final SubscriptionEntity subscriptionEntity = repo.findById(id)
         .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not Found."));

      final PricingEntity pricingEntity = pricingRepo.findByMemberShipEntity_Membership(dto.membership())
         .orElseThrow(() -> new PricingNotFoundException("Pricing Not Found."));

      subscriptionUpdater.apply(subscriptionEntity, pricingEntity);
      final SubscriptionEntity subscriptionModified = repo.saveAndFlush(subscriptionEntity);

      return subscriptionFactory.createFromEntity(subscriptionModified);
   }

   @Override
   @Transactional
   @CacheEvictSubscriptions
   @AuditLog(
      action = AuditLogAction.UPDATE,
      entityType = AuditLogEntityType.SUBSCRIPTION,
      entityId = "#id",
      result = "#result"
   )
   public SubscriptionResponse patch(long id) {
      final SubscriptionEntity subscription = repo.findById(id)
         .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not Found."));
      subscription.setFinished(true);
      subscription.setUpdatedAt(Instant.now());
      final SubscriptionEntity subscriptionFinalized = repo.saveAndFlush(subscription);
      return subscriptionFactory.createFromEntity(subscriptionFinalized);
   }

   @Transactional
   @Override
   @CacheEvictSubscriptions
   @AuditLog(
      action = AuditLogAction.UPDATE,
      entityType = AuditLogEntityType.SUBSCRIPTION,
      input = "#input",
      entityId = "#id",
      result = "#result"
   )
   public SubscriptionResponse put(long id, SubscriptionRequest input) {
      final SubscriptionEntity subscriptionEntity = validator.validateOnRenew(id, input);

      final PricingEntity pricing = pricingRepo.findByMemberShipEntity_Membership(input.membership())
         .orElseThrow(() -> new PricingNotFoundException("Pricing not found."));

      subscriptionUpdater.applyRenew(subscriptionEntity, pricing);
      final SubscriptionEntity subscriptionRenewed = repo.saveAndFlush(subscriptionEntity);
      return subscriptionFactory.createFromEntity(subscriptionRenewed);
   }

   @Override
   @Transactional
   @CacheEvictSubscriptions
   @AuditLog(
      action = AuditLogAction.DELETE,
      entityType = AuditLogEntityType.SUBSCRIPTION,
       entityId = "#id"
   )
   public void softDelete(long id) {
      repo.deleteById(id);
   }

}
