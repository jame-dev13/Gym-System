package com.jame.dev.gymApp.features.subscription.infrastructure.adapter;

import com.jame.dev.gymApp.features.subscription.application.dto.SubscriptionActor;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SubscriptionQueryJpaRepositoryAdapter implements SubscriptionQueryRepository {
   private final SubscriptionRepository subscriptionRepository;

   @Override
   public List<SubscriptionEntity> findAll() {
      return subscriptionRepository.findAll();
   }

   @Override
   public Page<SubscriptionEntity> findAll(Specification<SubscriptionEntity> specification, Pageable pageable) {
      return subscriptionRepository.findAll(specification, pageable);
   }

   @Override
   public Optional<SubscriptionEntity> findById(long id) {
      return subscriptionRepository.findById(id);
   }

   @Override
   public Optional<SubscriptionEntity> findByCustomerEmail(String email) {
      return subscriptionRepository.findByCustomerEmail(email);
   }

   @Override
   public Page<SubscriptionEntity> findAllByCustomerEmail(String email, Pageable pageable) {
      return subscriptionRepository.findAllByCustomer_User_Email(email, pageable);
   }

   @Override
   public Optional<Long> findIdByCustomerEmail(String email) {
      return subscriptionRepository.findIdByCustomerUserEmail(email);
   }

   @Override
   public Optional<SubscriptionActor> findSubscriptionActorById(Long subscriptionId) {
      return subscriptionRepository.findSubscriptionActorById(subscriptionId);
   }
}
