package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.aspects.annotations.aspects.CacheEvictSubscriptions;
import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.PricingEntity;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.exception.AlreadyExistsException;
import com.jame.dev.gymApp.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.exception.PricingNotFoundException;
import com.jame.dev.gymApp.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.factories.in.SubscriptionFactory;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.model.dto.in.SubscriptionFactoryDtoInput;
import com.jame.dev.gymApp.model.dto.out.PageDto;
import com.jame.dev.gymApp.model.dto.out.SubscriptionDtoOutput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.PricingRepository;
import com.jame.dev.gymApp.repository.SubscriptionRepository;
import com.jame.dev.gymApp.service.in.SubscriptionService;
import com.jame.dev.gymApp.shared.enums.CacheValues;
import com.jame.dev.gymApp.updaters.in.SubscriptionUpdater;
import com.jame.dev.gymApp.validators.SubscriptionValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class SubscriptionServiceImplementation implements SubscriptionService {
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
           key = "#pageable.pageNumber + ':' + #pageable.pageSize",
           unless = "#result == null"
   )
   public PageDto<SubscriptionDtoOutput> getPage(@NonNull Pageable pageable) {
      final Page<SubscriptionEntity> entityPage = repo.findAll(pageable);
      return subscriptionFactory.createPageFrom(entityPage);
   }

   @Override
   @Transactional
   @CacheEvict(value = CacheValues.SUBSCRIPTIONS, allEntries = true)
   public SubscriptionDtoOutput save(SubscriptionDtoInput dto) {
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
   @Transactional(readOnly = true)
   @Cacheable(value = CacheValues.SUBSCRIPTION, key = "#id")
   public Optional<SubscriptionDtoOutput> getById(long id) {
      final var entity = repo.findById(id)
              .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found."));
      return Optional.of(subscriptionFactory.createFromEntity(entity));
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
   @CacheEvictSubscriptions
   public SubscriptionDtoOutput update(long id, SubscriptionDtoInput dto) {
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
   public SubscriptionDtoOutput patch(long id) {
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
   public SubscriptionDtoOutput put(long id, SubscriptionDtoInput input) {
      final SubscriptionEntity subscriptionEntity = repo.findById(id)
              .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not Found."));

      validator.evaluateIncomingSubscription(input, subscriptionEntity);

      final PricingEntity pricing = pricingRepo.findByMemberShipEntity_Membership(input.membership())
              .orElseThrow(() -> new PricingNotFoundException("Pricing not found."));

      subscriptionUpdater.applyRenew(
              subscriptionEntity, pricing
      );
      final SubscriptionEntity subscriptionRenewed = repo.saveAndFlush(subscriptionEntity);
      return subscriptionFactory.createFromEntity(subscriptionRenewed);
   }

   @Override
   @Transactional
   @CacheEvictSubscriptions
   public void softDelete(long id) {
      repo.deleteById(id);
   }

}
