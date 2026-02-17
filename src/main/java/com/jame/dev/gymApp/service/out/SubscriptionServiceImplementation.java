package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.PeriodEntity;
import com.jame.dev.gymApp.entity.PricingEntity;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.exception.*;
import com.jame.dev.gymApp.factories.SubscriptionFactory;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.PricingRepository;
import com.jame.dev.gymApp.repository.SubscriptionRepository;
import com.jame.dev.gymApp.service.in.SubscriptionService;
import com.jame.dev.gymApp.updaters.SubscriptionUpdater;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
   private final SubscriptionFactory subscriptionFactory;
   private final SubscriptionUpdater subscriptionUpdater;

   @Override
   @Transactional(readOnly = true)
   public Page<@NonNull SubscriptionEntity> getPage(@NonNull Pageable pageable) {
      return repo.findAllByActiveTrue(pageable);
   }

   @Override
   @Transactional(readOnly = true)
   public Optional<SubscriptionEntity> getById(@NonNull Long id) {
      return repo.findById(id);
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

   @Transactional
   @Override
   public SubscriptionEntity save(@NonNull SubscriptionDtoInput dto) {
      final CustomerEntity customer = customerRepo.findByUser_Email(dto.customerEmail())
              .map(c -> {
                 if (!c.isActive()) {
                    throw new NoActiveException("Customer is active");
                 }
                 return c;
              })
              .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found."));

      if (repo.existsByCustomer(customer)) {
         throw new AlreadyExistsException("There's a subscription linked to the customer with: " + dto.customerEmail());
      }

      final PricingEntity pricing = pricingRepo.findByMemberShipEntity_Membership(dto.membership())
              .orElseThrow(() -> new PricingNotFoundException("Pricing Not Found."));
      final SubscriptionEntity subscription = subscriptionFactory.createFrom(dto, customer, pricing, LocalDate.now());
      subscription.setCreatedAt(Instant.now());
      return repo.save(subscription);
   }

   @Transactional
   @Override
   public SubscriptionEntity update(Long id, @NonNull SubscriptionDtoInput dto) {
      final SubscriptionEntity subscriptionEntity = repo.findById(id)
              .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not Found."));
      final PricingEntity pricingEntity = pricingRepo.findByMemberShipEntity_Membership(dto.membership())
              .orElseThrow(() -> new PricingNotFoundException("Pricing Not Found."));
      subscriptionEntity.setPricing(pricingEntity);
      subscriptionEntity.setUpdatedAt(Instant.now());
      return repo.save(subscriptionEntity);
   }

   @Transactional
   @Override
   public SubscriptionEntity patch(Long id) {
      return this.finalizeSubscription(id);
   }

   @Transactional
   @Override
   public SubscriptionEntity put(@NonNull Long id, @NonNull SubscriptionDtoInput input) {
      return renew(id, input);
   }

   @Transactional
   @Override
   public void softDelete(@NonNull Long id) {
      repo.deleteById(id);
   }

   private SubscriptionEntity finalizeSubscription(@NonNull Long id) {
      final SubscriptionEntity subscription = repo.findById(id)
              .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not Found."));
      subscription.setFinished(true);
      subscription.setUpdatedAt(Instant.now());
      return repo.save(subscription);
   }

   private SubscriptionEntity renew(long id, SubscriptionDtoInput input) {
      final SubscriptionEntity subscriptionEntity = repo.findById(id)
              .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not Found."));
      final String email = subscriptionEntity.getCustomer().getUser().getEmail();
      if (!Objects.equals(email, input.customerEmail())) {
         throw new RenewSubscriptionException("Customer doesn't match.");
      }
      final PeriodEntity currentPeriod = subscriptionEntity.getSubscriptionPeriods().getLast();
      if (!canRenew(currentPeriod, subscriptionEntity)) {
         throw new RenewSubscriptionException("Can't renew the subscription yet.");
      }
      final PricingEntity pricing = pricingRepo.findByMemberShipEntity_Membership(input.membership())
              .orElseThrow(() -> new PricingNotFoundException("Pricing not found."));

      final SubscriptionEntity subscriptionRenew = subscriptionUpdater.apply(
              subscriptionEntity, pricing, currentPeriod.getEndPeriod()
      );
      subscriptionRenew.setUpdatedAt(Instant.now());
      return repo.save(subscriptionRenew);
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
}
