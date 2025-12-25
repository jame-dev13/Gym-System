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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
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
      final boolean existsCustomer = repo.existsByCustomer_IdAndFinishedFalse(dto.customerId());
      if (existsCustomer) {
         throw new AlreadyExistsException("Customer has an active subscription.");
      }
      final CustomerEntity customer = customerRepo.findById(dto.customerId())
              .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found."));
      final PricingEntity pricing = pricingRepo.findById(dto.pricingId())
              .orElseThrow(() -> new PricingNotFoundException("Pricing Not Found."));
      final PeriodEntity period = buildPeriods(pricing, LocalDate.now());
      final List<PeriodEntity> periods = List.of(period);
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
   public SubscriptionEntity update(@NonNull Long id, @NonNull SubscriptionDtoInput input) {
      return renew(id, input);
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

   @Transactional
   private SubscriptionEntity renew(long id, SubscriptionDtoInput input) {
      final SubscriptionEntity subscriptionEntity = repo.findById(id)
              .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not Found."));
      if (!Objects.equals(subscriptionEntity.getCustomer().getId(), input.customerId())) {
         throw new RenewSubscriptionException("Customer doesn't match.");
      }
      final PeriodEntity currentPeriod = subscriptionEntity.getSubscriptionPeriods().getLast();
      if (!canRenew(currentPeriod, subscriptionEntity)) {
         throw new RenewSubscriptionException("Can't renew the subscription yet.");
      }
      final PricingEntity pricing = pricingRepo.findById(input.pricingId())
              .orElseThrow(() -> new PricingNotFoundException("Pricing not found."));

      final long days = extractDays(currentPeriod.getEndPeriod());
      final LocalDate startDate = (days <= 0) ? LocalDate.now() : LocalDate.now().plusDays(days);

      final PeriodEntity period = buildPeriods(pricing, startDate);

      final List<PeriodEntity> periods = subscriptionEntity.getSubscriptionPeriods();
      periods.add(period);

      subscriptionEntity.setPricing(pricing);
      subscriptionEntity.setSubscriptionPeriods(periods);
      subscriptionEntity.setFinished(false);

      return repo.save(subscriptionEntity);
   }

   private Period extractPeriod(PricingEntity pricing) {
      final String valueName = pricing.getMemberShipEntity().getMembership().name();
      final Optional<Period> periodOptional = Optional.of(Period.valueOf(valueName));
      return periodOptional.orElseThrow(() -> new IllegalArgumentException("No period value present for: " + valueName));
   }

   private PeriodEntity buildPeriods(final PricingEntity pricing, final LocalDate startDate) {
      final Period period = extractPeriod(pricing);
      return new PeriodEntity(period, startDate);
   }

   private boolean canRenew(final PeriodEntity period, final SubscriptionEntity subscriptionEntity) {
      if(subscriptionEntity.isFinished()) return true;
      final int WINDOW = 4;
      final LocalDate now = LocalDate.now();
      final LocalDate finishPeriodDate = period.getEndPeriod();
      if(now.isAfter(finishPeriodDate)) return true;
      final long windowAccept = ChronoUnit.DAYS.between(now, finishPeriodDate);
      return windowAccept < WINDOW;
   }

   private long extractDays(final LocalDate finishDate) {
      return ChronoUnit.DAYS.between(finishDate, LocalDate.now());
   }
}
