package com.jame.dev.gymApp.features.subscription.infrastructure.listener;

import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationMutationRepository;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationQueryRepository;
import com.jame.dev.gymApp.features.subscription.domain.event.SubscriptionFinalizedEvent;
import com.jame.dev.gymApp.features.subscription.domain.event.SubscriptionMutationEvent;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class SubscriptionMutationListener {
   private final SubscriberNotificationQueryRepository notificationQueryRepository;
   private final SubscriberNotificationMutationRepository mutationRepository;
   private final SubscriptionQueryRepository subscriptionQueryRepository;

   @TransactionalEventListener(value = SubscriptionMutationEvent.class, phase = TransactionPhase.AFTER_COMMIT)
   @Transactional(propagation = Propagation.REQUIRES_NEW)
   @Async("taskExecutor")
   public void onSubscriptionMutated(final SubscriptionMutationEvent event) {
      final Function<Integer, LocalDateTime> nextNotificationDate = days -> event.endingPeriodDate().minusDays(days).atStartOfDay();
      notificationQueryRepository.findBySubscriptionId(event.subscriptionId())
         .ifPresentOrElse(notificationQuery -> {
               notificationQuery.setNextNotificationDate(nextNotificationDate.apply(notificationQuery.getRangeNotificationDays()));
               mutationRepository.save(notificationQuery);
            },
            () -> {
               final int DEFAULT_DAYS = 7;
               final SubscriptionEntity subscription = subscriptionQueryRepository.findById(event.subscriptionId())
                  .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for id: " + event.subscriptionId()));
               mutationRepository.save(
                  SubscriberNotificationEntity.builder()
                     .subscription(subscription)
                     .nextNotificationDate(nextNotificationDate.apply(DEFAULT_DAYS))
                     .build()
               );
            });
   }

   @TransactionalEventListener(value = SubscriptionFinalizedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
   @Transactional(propagation = Propagation.REQUIRES_NEW)
   @Async("taskExecutor")
   public void onSubscriptionFinalized(final SubscriptionFinalizedEvent event) {
      notificationQueryRepository.findBySubscriptionId(event.subscriptionId())
         .ifPresent(notificationQuery -> {
            notificationQuery.setNextNotificationDate(null);
            mutationRepository.save(notificationQuery);
         });
   }
}
