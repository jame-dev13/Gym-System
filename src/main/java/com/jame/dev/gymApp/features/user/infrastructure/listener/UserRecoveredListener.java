package com.jame.dev.gymApp.features.user.infrastructure.listener;

import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
import com.jame.dev.gymApp.features.user.domain.event.UserRecoveredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserRecoveredListener {

   private final CustomerMutationRepository customerRepository;

   @Transactional
   @EventListener(UserRecoveredEvent.class)
   @Async("taskExecutor")
   public void recoverCustomerAssociated(final UserRecoveredEvent event) {
      customerRepository.activateCustomerByUserId(event.userId());
   }
}
