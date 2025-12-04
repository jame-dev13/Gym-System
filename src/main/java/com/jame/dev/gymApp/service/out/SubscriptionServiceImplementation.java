package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.PeriodEntity;
import com.jame.dev.gymApp.entity.PricingEntity;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.exception.*;
import com.jame.dev.gymApp.mapper.SubscriptionMapper;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.PeriodRepository;
import com.jame.dev.gymApp.repository.PricingRepository;
import com.jame.dev.gymApp.repository.SubscriptionRepository;
import com.jame.dev.gymApp.service.in.SubscriptionService;
import com.jame.dev.gymApp.shared.enums.Period;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
   private final PeriodRepository periodRepository;
   private final SubscriptionMapper subscriptionMapper;

   @Override
   public List<SubscriptionEntity> getAll() {
      return repo.findByActiveTrue();
   }

   @Override
   public Optional<SubscriptionEntity> getById(@NonNull Long id) {
      return repo.findById(id);
   }

   @Transactional
   @Override
   public SubscriptionEntity save(@NonNull SubscriptionDtoInput dto) {
      boolean existsCustomer = repo.existsByCustomer_IdAndActiveTrue(dto.customerId());
      if(existsCustomer){
         throw new AlreadyExistsException("Customer has an active subscription.");
      }
      CustomerEntity customer = customerRepo.findById(dto.customerId())
              .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found."));
      PricingEntity pricing = pricingRepo.findById(dto.pricingId())
              .orElseThrow(() -> new PricingNotFoundException("Pricing Not Found."));
      Period period = switch (pricing.getMemberShipEntity().getMembership()){
         case BIWEEKLY -> Period.FORTNIGHTLY;
         case MONTHLY -> Period.MONTHLY;
         case QUARTERLY -> Period.QUARTERLY;
         case ANNUAL -> Period.ANNUAL;
         case null -> throw new PeriodNotFoundException("Not mapping for : " + pricing.getMemberShipEntity());
      };
      List<PeriodEntity> periods = List.of(new PeriodEntity(period, LocalDate.now()));
      SubscriptionEntity subscription = subscriptionMapper.toEntity(dto, customer, pricing, periods);
      return repo.save(subscription);
   }

   @Transactional
   @Override
   public SubscriptionEntity update(@NonNull Long id, @NonNull SubscriptionDtoInput dto) {
      throw new NoOperationException("Unsupported Operation.");
   }

   @Transactional
   @Override
   public SubscriptionEntity finalizeSubscription(@NonNull Long id) {
      SubscriptionEntity subscription = repo.findById(id)
              .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not Found."));
      subscription.setFinished(true);
      return repo.save(subscription);
   }

   @Transactional
   @Override
   public void softDeleteById(@NonNull Long id) {
      repo.softDelete(id);
   }
}
