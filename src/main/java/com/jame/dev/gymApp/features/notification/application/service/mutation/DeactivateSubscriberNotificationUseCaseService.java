package com.jame.dev.gymApp.features.notification.application.service.mutation;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.contract.SubscriberNotificationFactory;
import com.jame.dev.gymApp.features.notification.application.usecases.mutation.DeactivateSubscriberNotificationUseCase;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationMutationRepository;
import com.jame.dev.gymApp.features.notification.infrastructure.annotation.CheckSubscriber;
import com.jame.dev.gymApp.features.notification.infrastructure.annotation.EvictSubscriberNotification;
import com.jame.dev.gymApp.features.notification.infrastructure.query.SubscriberNotifiableRetrieverRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class DeactivateSubscriberNotificationUseCaseService implements DeactivateSubscriberNotificationUseCase {
   private final SubscriberNotifiableRetrieverRepository retrieverRepository;
   private final SubscriberNotificationMutationRepository mutationRepository;
   private final SubscriberNotificationFactory factory;

   @Override
   @Transactional
   @CheckSubscriber
   @EvictSubscriberNotification
   public SubscriberNotificationResponse deactivateNotification(AuthPrincipal principal) {
      final var notificationEntity = retrieverRepository.findByCurrentUsername(principal.username())
         .orElseThrow(() -> new NotFoundException("Notification record not found for: " + principal.username()));

      if (!notificationEntity.isNotifiable())
         return factory.createFromEntity(notificationEntity);

      notificationEntity.setNotifiable(false);
      final var notificationNotifiable = mutationRepository.save(notificationEntity);
      return factory.createFromEntity(notificationNotifiable);
   }
}
