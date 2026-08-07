package com.jame.dev.gymApp.features.audit.application.support.resolver;

import com.jame.dev.gymApp.features.audit.application.model.SubscriptionBeforeUpdateModel;
import com.jame.dev.gymApp.features.audit.application.model.UserBeforeUpdateModel;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.customer.infrastructure.persistence.CustomerRepository;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.SubscriptionRepository;
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
            .map(u -> UserBeforeUpdateModel.builder()
               .name(u.getName())
               .email(u.getEmail())
               .roles(roleMapper.toRoleSet(u.getRoles()))
               .build())
            .orElseThrow(() -> new UserEntityNotFoundException("User entity not found."));
         case CUSTOMER -> customerRepository.findById(id)
            .map(c -> new CustomerRequest(c.getUser().getEmail(), c.getPhoneContact()))
            .orElseThrow(() -> new CustomerNotFoundException("Customer entity not found."));
         case SUBSCRIPTION -> subscriptionRepository.findById(id)
            .map(s -> SubscriptionBeforeUpdateModel.builder()
               .membership(s.getPricing().getMemberShipEntity().getMembership())
               .price(s.getPricing().getPrice())
               .status(s.getStatus())
               .build())
            .orElseThrow(() -> new SubscriptionNotFoundException("Subscription entity not found."));
         case NO_SET -> throw new IllegalArgumentException("Type not accepted.");
      };
   }
}
