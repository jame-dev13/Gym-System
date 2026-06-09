package com.jame.dev.gymApp.features.audit.application.support.resolver;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerRepository;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionRepository;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.application.support.mapper.RoleMapper;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditIdQueryBeforeResolver {
   private final UserRepository userRepository;
   private final CustomerRepository customerRepository;
   private final SubscriptionRepository subscriptionRepository;
   private final RoleMapper roleMapper;

   public Object getStateBeforeOfById(AuditLogEntityType type, long id) {
      return switch (type) {
         case USER -> userRepository.findById(id)
            .map(u -> new UserRequest(
               u.getName(), u.getEmail(),
               null, u.getProvider(),
               roleMapper.toRoleSet(u.getRoles())))
            .orElseThrow(() -> new UserEntityNotFoundException("User entity not found."));
         case CUSTOMER -> customerRepository.findById(id)
            .map(c -> new CustomerRequest(c.getUser().getEmail(), c.getPhoneContact()))
            .orElseThrow(() -> new CustomerNotFoundException("Customer entity not found."));
         case SUBSCRIPTION -> subscriptionRepository.findById(id)
            .map(s -> new SubscriptionRequest(
               s.getCustomer().getUser().getEmail(),
               s.getPricing().getMemberShipEntity().getMembership())
            )
            .orElseThrow(() -> new SubscriptionNotFoundException("Subscription entity not found."));
         case NO_SET -> throw new IllegalArgumentException("Type not accepted.");
      };
   }
}
