package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.PeriodEntity;
import com.jame.dev.gymApp.entity.PricingEntity;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.exception.*;
import com.jame.dev.gymApp.mapper.SubscriptionMapper;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.PricingRepository;
import com.jame.dev.gymApp.repository.SubscriptionRepository;
import com.jame.dev.gymApp.service.in.SubscriptionService;
import com.jame.dev.gymApp.shared.enums.Period;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImplementation implements SubscriptionService {
   private final SubscriptionRepository repo;
   private final CustomerRepository customerRepo;
   private final PricingRepository pricingRepo;
   private final SubscriptionMapper subscriptionMapper;


   @Override
   public Page<@NonNull SubscriptionEntity> getPage(@NonNull Pageable pageable) {
      return repo.findAllByActiveTrue(pageable);
   }

   @Transactional
   @Override
   public SubscriptionEntity save(@NonNull SubscriptionDtoInput dto) {
      final boolean existsCustomer = repo.existsByCustomer_IdAndActiveTrue(dto.customerId());
      if (existsCustomer) {
         throw new AlreadyExistsException("Customer has an active subscription.");
      }
      final CustomerEntity customer = customerRepo.findById(dto.customerId())
              .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found."));
      final PricingEntity pricing = pricingRepo.findById(dto.pricingId())
              .orElseThrow(() -> new PricingNotFoundException("Pricing Not Found."));
      final Period period = this.extractPeriod(pricing);
      final List<PeriodEntity> periods = List.of(new PeriodEntity(period, LocalDate.now()));
      final SubscriptionEntity subscription = subscriptionMapper.toEntity(dto, customer, pricing, periods);
      return repo.save(subscription);
   }

   @Override
   public Optional<SubscriptionEntity> getById(@NonNull Long id) {
      return repo.findById(id);
   }

   @Transactional
   @Override
   public SubscriptionEntity patch(Long id) {
      return this.finalizeSubscription(id);
   }

   @Transactional
   @Override
   public void softDelete(@NonNull Long id) {
      repo.softDelete(id);
   }

   @Transactional
   private SubscriptionEntity finalizeSubscription(@NonNull Long id) {
      final SubscriptionEntity subscription = repo.findById(id)
              .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not Found."));
      subscription.setFinished(true);
      return repo.save(subscription);
   }

   private Period extractPeriod(PricingEntity pricing) {
      final String valueName = pricing.getMemberShipEntity().getMembership().name();
      final Optional<Period> periodOptional = Optional.of(Period.valueOf(valueName));
      return periodOptional.orElseThrow(() -> new IllegalArgumentException("No period value present for: " + valueName));
   }
}
