package com.jame.dev.gymApp.validators;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.EmailNotFoundException;
import com.jame.dev.gymApp.exception.MissMatchException;
import com.jame.dev.gymApp.exception.RenewSubscriptionException;
import com.jame.dev.gymApp.exception.SubscriptionUnfinishedException;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

@Component
public class SubscriptionValidator {

   private final static int WINDOW_DAYS_RENEW = 4;

   public void evaluateIncomingSubscription(final SubscriptionDtoInput input, final SubscriptionEntity subscription) {

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
