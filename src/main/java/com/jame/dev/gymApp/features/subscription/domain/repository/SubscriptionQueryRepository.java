package com.jame.dev.gymApp.features.subscription.domain.repository;

import com.jame.dev.gymApp.features.subscription.application.dto.SubscriptionActor;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface SubscriptionQueryRepository {

   List<SubscriptionEntity> findAll();

   Page<SubscriptionEntity> findAll(final Specification<SubscriptionEntity> specification, final Pageable pageable);

   Optional<SubscriptionEntity> findById(final long id);

   Optional<SubscriptionEntity> findByCustomerEmail(final String email);

   Page<SubscriptionEntity> findAllByCustomerEmail(final String email, final Pageable pageable);

   Optional<Long> findIdByCustomerEmail(final String email);

   Optional<SubscriptionActor> findSubscriptionActorById(final Long subscriptionId);
}
