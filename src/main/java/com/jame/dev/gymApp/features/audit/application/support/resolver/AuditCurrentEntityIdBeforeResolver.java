package com.jame.dev.gymApp.features.audit.application.support.resolver;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.infrastructure.security.principal.IdentityExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditCurrentEntityIdBeforeResolver {
   private final CustomerQueryRepository customerQueryRepository;
   private final SubscriptionQueryRepository subscriptionQueryRepository;
   private final IdentityExtractorService identityExtractor;

   public Long getEntityIdByCurrentAuthentication(final Authentication auth, final AuditLogEntityType type) {
      final String subject = identityExtractor.extract(auth);
      return switch (type) {
         case CUSTOMER -> customerQueryRepository.findIdByUserEmail(subject)
            .orElseThrow(() -> new NotFoundException("Customer id not found."));
         case SUBSCRIPTION -> subscriptionQueryRepository.findIdByCustomerEmail(subject)
            .orElseThrow(() -> new NotFoundException("Subscription id not found."));
         default -> throw new IllegalArgumentException("Type unacceptable here: " + type);
      };
   }
}
