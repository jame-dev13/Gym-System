package com.jame.dev.gymApp.features.audit.application.support.resolver;

import com.jame.dev.gymApp.application.service.IdentityExtractorApplicationService;
import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditCurrentEntityIdBeforeResolver {

   private final CustomerQueryRepository customerQueryRepository;
   private final SubscriptionQueryRepository subscriptionQueryRepository;
   private final IdentityExtractorApplicationService identityExtractorApplicationService;

   public Long getEntityIdByCurrentAuthentication(final Authentication auth, final AuditLogEntityType type) {
      final String subject = identityExtractorApplicationService.extract(auth);
      return switch (type) {
         case CUSTOMER -> customerQueryRepository.findByUserEmail(subject)
            .map(CustomerEntity::getId)
            .orElseThrow(() -> new NotFoundException("Customer id not found."));
         case SUBSCRIPTION -> subscriptionQueryRepository.findByCustomerEmail(subject)
            .map(SubscriptionEntity::getId)
            .orElseThrow(() -> new NotFoundException("Subscription id not found."));
         default -> throw new IllegalArgumentException("Type unacceptable here: " + type);
      };
   }
}
