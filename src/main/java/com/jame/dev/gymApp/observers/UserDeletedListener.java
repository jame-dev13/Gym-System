package com.jame.dev.gymApp.observers;

import com.jame.dev.gymApp.model.listeners.UserDeletedEvent;
import com.jame.dev.gymApp.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDeletedListener {
   private final CustomerRepository customerRepository;

   @EventListener
   public void handleUserDeletedEvent(final @NonNull UserDeletedEvent event) {
      customerRepository.deleteByUserId(event.userId());
   }
}
