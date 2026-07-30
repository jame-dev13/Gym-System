package com.jame.dev.gymApp.features.notification.application.service.mutation;

import com.jame.dev.gymApp.features.notification.application.usecases.mutation.DeleteSubscriberNotificationById;
import com.jame.dev.gymApp.features.notification.domain.exception.NotificationException;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationMutationRepository;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationValidationRepository;
import com.jame.dev.gymApp.features.notification.infrastructure.annotation.EvictSubscriberNotification;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class DeleteSubscriberNotificationByIdService implements DeleteSubscriberNotificationById {
   private final SubscriberNotificationValidationRepository subscriberNotificationValidationRepository;
   private final SubscriberNotificationMutationRepository subscriberNotificationMutationRepository;

   @Override
   @Transactional
   @EvictSubscriberNotification
   public void deleteSubscriberNotificationById(UUID uuid) {
      if (!subscriberNotificationValidationRepository.existsById(uuid)) {
         throw new NotificationException("Subscriber notification not found for id: " + uuid);
      }
      subscriberNotificationMutationRepository.deleteById(uuid);
   }
}
