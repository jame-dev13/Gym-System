package com.jame.dev.gymApp.features.notification.application.service.mutation;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.notification.api.request.DayRangeRequest;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.contract.SubscriberNotificationFactory;
import com.jame.dev.gymApp.features.notification.application.usecases.mutation.ChangeDayRangeSubscriberNotificationUseCase;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationMutationRepository;
import com.jame.dev.gymApp.features.notification.infrastructure.annotation.CheckSubscriber;
import com.jame.dev.gymApp.features.notification.infrastructure.annotation.EvictSubscriberNotification;
import com.jame.dev.gymApp.features.notification.infrastructure.query.SubscriberNotifiableRetrieverRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class ChangeDayRangeSubscriberNotificationUseCaseService implements ChangeDayRangeSubscriberNotificationUseCase {
   private final SubscriberNotifiableRetrieverRepository retrieverRepository;
   private final SubscriberNotificationMutationRepository mutationRepository;
   private final SubscriberNotificationFactory factory;

   @Override
   @Transactional
   @CheckSubscriber
   @EvictSubscriberNotification
   public SubscriberNotificationResponse changeDayRange(AuthPrincipal principal, DayRangeRequest request) {
      final var notificationEntity = retrieverRepository.findByCurrentUsername(principal.username())
         .orElseThrow(() -> new NotFoundException("Notification record not found for: " + principal.username()));

      if (request.numberOfDays() == notificationEntity.getRangeNotificationDays())
         return factory.createFromEntity(notificationEntity);

      final var notificationDate = Objects.requireNonNull(notificationEntity.getNextNotificationDate(), "Next notification date found as undefined.");

      final int difference = notificationEntity.getRangeNotificationDays() - request.numberOfDays();

      notificationEntity.setRangeNotificationDays(request.numberOfDays());

      final int absDifference = Math.abs(difference);
      notificationEntity.setNextNotificationDate(
         difference < 0 ? notificationDate.minusDays(absDifference) : notificationDate.plusDays(absDifference)
      );

      final var notificationNotifiable = mutationRepository.save(notificationEntity);
      return factory.createFromEntity(notificationNotifiable);
   }
}
