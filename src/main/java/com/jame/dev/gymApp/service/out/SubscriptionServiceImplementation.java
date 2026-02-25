package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.aspects.annotations.CacheEvictSubscriptions;
import com.jame.dev.gymApp.entity.*;
import com.jame.dev.gymApp.exception.*;
import com.jame.dev.gymApp.factories.in.Factory;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.model.dto.in.SubscriptionFactoryDtoInput;
import com.jame.dev.gymApp.model.dto.out.PageDto;
import com.jame.dev.gymApp.model.dto.out.SubscriptionDtoOutput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.PricingRepository;
import com.jame.dev.gymApp.repository.SubscriptionRepository;
import com.jame.dev.gymApp.service.in.SubscriptionService;
import com.jame.dev.gymApp.shared.enums.CacheValues;
import com.jame.dev.gymApp.updaters.SubscriptionUpdater;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImplementation implements SubscriptionService {
   private final SubscriptionRepository repo;
   private final CustomerRepository customerRepo;
   private final PricingRepository pricingRepo;
   private final Factory<SubscriptionEntity, SubscriptionDtoOutput, SubscriptionFactoryDtoInput> subscriptionFactory;
   private final SubscriptionUpdater subscriptionUpdater;

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
   public SubscriptionDtoOutput save(@NonNull SubscriptionDtoInput dto) {
      final CustomerEntity customer = customerRepo.findByUser_Email(dto.customerEmail())
              .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found."));

      if (repo.existsByCustomer(customer)) {
         throw new AlreadyExistsException("There's a subscription linked to the customer with: " + dto.customerEmail());
      }

      final PricingEntity pricing = pricingRepo.findByMemberShipEntity_Membership(dto.membership())
              .orElseThrow(() -> new PricingNotFoundException("Pricing Not Found."));

      final SubscriptionEntity subscriptionSaved = repo.saveAndFlush(
              subscriptionFactory.createFromInput(
                      new SubscriptionFactoryDtoInput(
                              dto, customer, pricing, LocalDate.now()
                      )
              )
      );

      return subscriptionFactory.createFromEntity(subscriptionSaved);
   }

   @Override
   @Transactional
   @CacheEvictSubscriptions
   public SubscriptionDtoOutput patch(Long id) {
      final SubscriptionEntity subscription = repo.findById(id)
              .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not Found."));
      subscription.setFinished(true);
      subscription.setUpdatedAt(Instant.now());
      final SubscriptionEntity subscriptionFinalized = repo.saveAndFlush(subscription);
      return subscriptionFactory.createFromEntity(subscriptionFinalized);
   }

   @Override
   @Transactional
   @CacheEvictSubscriptions
   public SubscriptionDtoOutput update(Long id, @NonNull SubscriptionDtoInput dto) {
      final SubscriptionEntity subscriptionEntity = repo.findById(id)
              .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not Found."));

      final PricingEntity pricingEntity = pricingRepo.findByMemberShipEntity_Membership(dto.membership())
              .orElseThrow(() -> new PricingNotFoundException("Pricing Not Found."));

      subscriptionUpdater.apply(subscriptionEntity, pricingEntity);
      final SubscriptionEntity subscriptionModified = repo.saveAndFlush(subscriptionEntity);

      return subscriptionFactory.createFromEntity(subscriptionModified);
   }

   @Transactional
   @Override
   @CacheEvictSubscriptions
   public SubscriptionDtoOutput put(@NonNull Long id, @NonNull SubscriptionDtoInput input) {
      return renew(id, input);
   }

   @Override
   @Transactional
   @CacheEvictSubscriptions
   public void softDelete(@NonNull Long id) {
      repo.deleteById(id);
   }

   @Override
   @Transactional(readOnly = true)
   @Cacheable(value = CacheValues.SUBSCRIPTION, key = "#id")
   public Optional<SubscriptionDtoOutput> getById(@NonNull Long id) {
      final var entity = repo.findById(id);
      return entity.isPresent() ?
              entity.map(subscriptionFactory::createFromEntity) : Optional.empty();
   }

   @Override
   @Transactional(readOnly = true)
   public Optional<SubscriptionEntity> getByEmail(String email) {
      return repo.findActiveSubscriptionByEmail(email);
   }

   @Override
   @Transactional(readOnly = true)
   public boolean exitsByIdAndCustomerEmail(long id, String email) {
      return repo.existsByIdAndCustomer_User_EmailAndActiveTrue(id, email);
   }

   private SubscriptionDtoOutput renew(long id, final SubscriptionDtoInput input) {
      final SubscriptionEntity subscriptionEntity = repo.findById(id)
              .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not Found."));

      if (!subscriptionEntity.isFinished()) {
         throw new RenewSubscriptionException("Subscription unfinished, cannot renew yet.");
      }

      final String email = extractEmailFromSubscriptionCustomer(subscriptionEntity);
      if (!Objects.equals(email, input.customerEmail())) {
         throw new RenewSubscriptionException("Customer doesn't match.");
      }
      final PeriodEntity currentPeriod = subscriptionEntity.getSubscriptionPeriods().getLast();
      if (!canRenew(currentPeriod, subscriptionEntity)) {
         throw new RenewSubscriptionException("Can't renew the subscription yet.");
      }
      final PricingEntity pricing = pricingRepo.findByMemberShipEntity_Membership(input.membership())
              .orElseThrow(() -> new PricingNotFoundException("Pricing not found."));

      subscriptionUpdater.applyRenew(
              subscriptionEntity, pricing, currentPeriod.getEndPeriod()
      );
      final SubscriptionEntity subscriptionRenewed = repo.saveAndFlush(subscriptionEntity);
      return subscriptionFactory.createFromEntity(subscriptionRenewed);
   }

   private boolean canRenew(final PeriodEntity period, final SubscriptionEntity subscriptionEntity) {
      if (subscriptionEntity.isFinished()) return true;
      final int WINDOW = 4;
      final LocalDate now = LocalDate.now();
      final LocalDate finishPeriodDate = period.getEndPeriod();
      if (now.isAfter(finishPeriodDate)) return true;
      final long windowAccept = ChronoUnit.DAYS.between(now, finishPeriodDate);
      return windowAccept < WINDOW;
   }

   private String extractEmailFromSubscriptionCustomer(SubscriptionEntity entity) {
      return Optional.of(entity)
              .map(SubscriptionEntity::getCustomer)
              .map(CustomerEntity::getUser)
              .map(UserEntity::getEmail)
              .orElseThrow(() -> new EmailNotFoundExceptuon("Customer not identified."));
   }
}
