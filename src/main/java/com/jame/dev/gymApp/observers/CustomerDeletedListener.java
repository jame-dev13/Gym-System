package com.jame.dev.gymApp.observers;

import com.jame.dev.gymApp.model.listeners.CustomerDeletedEvent;
import com.jame.dev.gymApp.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerDeletedListener {
   private final SubscriptionRepository subscriptionRepository;

   @EventListener
   public void handleOnCustomerDelete(final @NonNull CustomerDeletedEvent event) {
      subscriptionRepository.deleteByCustomerId(event.customerId());
   }
}
