package com.jame.dev.gymApp.features.user.infrastructure.listener;

import com.jame.dev.gymApp.features.user.domain.event.UserRecoveredEvent;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserRecoveredListener {

   private final CustomerRepository customerRepository;

   @Transactional
   @EventListener(UserRecoveredEvent.class)
   public void recoverCustomerAssociated(final UserRecoveredEvent event) {
      customerRepository.activateByUserId(event.userId());
   }
}
