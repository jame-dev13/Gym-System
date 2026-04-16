package com.jame.dev.gymApp.validators;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.*;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {
   private SubscriptionRepository subscriptionRepository;
   private final static int WINDOW_DAYS_RENEW = 4;

   public SubscriptionEntity validateOnRenew(
      final long id,
      final SubscriptionDtoInput input) {

      final SubscriptionEntity subscription = subscriptionRepository.findById(id)
         .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not Found."));

      if (!subscription.isFinished()) {
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
      if (subscription.isFinished()) return true;
      final var currentPeriod = subscription.getSubscriptionPeriods().getLast();
      final var subscriptionFinishDate = currentPeriod.getEndPeriod();
      final LocalDate now = LocalDate.now();
      if (now.isAfter(subscriptionFinishDate)) return true;
      final long daysUntil = ChronoUnit.DAYS.between(now, subscriptionFinishDate);
      return daysUntil <= WINDOW_DAYS_RENEW;
   }
}
