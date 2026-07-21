package com.jame.dev.gymApp.features.subscription.application.support.validator;

import com.jame.dev.gymApp.domain.exception.EmailNotFoundException;
import com.jame.dev.gymApp.domain.exception.MissMatchException;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.domain.exception.RenewSubscriptionException;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionUnfinishedException;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionStatus;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {
   private final SubscriptionQueryRepository subscriptionRepository;
   private final static int WINDOW_DAYS_RENEW = 4;

   public SubscriptionEntity validateOnRenew(
      final long id,
      final SubscriptionRequest input) {

      final SubscriptionEntity subscription = subscriptionRepository.findById(id)
         .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not Found."));

      if (subscription.getStatus() != SubscriptionStatus.FINALIZED) {
         throw new SubscriptionUnfinishedException("Subscription unfinished, cannot renew yet.");
      }

      final String email = Optional.of(subscription.getCustomer())
         .map(CustomerEntity::getUser)
         .map(UserEntity::getEmail)
         .orElseThrow(() -> new EmailNotFoundException("Customer not identified."));
      if (!Objects.equals(email, input.customerEmail())) {
         throw new MissMatchException("Customer doesn't match.");
      }

      if (!canRenewSubscription(subscription)) {
         throw new RenewSubscriptionException("Can't renew the subscription yet.");
      }
      return subscription;
   }

   public boolean canRenewSubscription(final SubscriptionEntity subscription) {
      if (subscription.getStatus() == SubscriptionStatus.FINALIZED) return true;
      final var currentPeriod = subscription.getSubscriptionPeriods().getLast();
      final var subscriptionFinishDate = currentPeriod.getEndPeriod();
      final LocalDate now = LocalDate.now();
      if (now.isAfter(subscriptionFinishDate)) return true;
      final long daysUntil = ChronoUnit.DAYS.between(now, subscriptionFinishDate);
      return daysUntil <= WINDOW_DAYS_RENEW;
   }
}
