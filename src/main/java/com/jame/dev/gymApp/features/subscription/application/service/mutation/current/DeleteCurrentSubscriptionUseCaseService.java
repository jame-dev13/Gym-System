package com.jame.dev.gymApp.features.subscription.application.service.mutation.current;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.current.DeleteCurrentSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.annotations.EvictCurrentOnDeleteSub;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.features.auth.infrastructure.security.identity.IdentityExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class DeleteCurrentSubscriptionUseCaseService implements DeleteCurrentSubscriptionUseCase {
   private final SubscriptionMutationRepository subscriptionMutationRepository;
   private final IdentityExtractorService identityExtractorService;

   @Override
   @Transactional
   @AuditLog(
      action = AuditLogAction.DELETE,
      entityType = AuditLogEntityType.SUBSCRIPTION,
      input = "#authentication"
   )
   @EvictCurrentOnDeleteSub
   public void delete(Authentication authentication) {
      final String customerEmail = identityExtractorService.extract(authentication);
      subscriptionMutationRepository.deleteByCustomerEmail(customerEmail);
   }
}
