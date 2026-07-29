package com.jame.dev.gymApp.features.subscription.application.service.mutation;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.SoftDeleteSubscriptionByIdUseCase;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.annotations.EvictSubsOnDrop;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class SoftDeleteSubscriptionByIdUseCaseService implements SoftDeleteSubscriptionByIdUseCase {
   private final SubscriptionMutationRepository subscriptionMutationRepository;

   @Override
   @Transactional
   @EvictSubsOnDrop
   @AuditLog(
      action = AuditLogAction.DELETE,
      entityType = AuditLogEntityType.SUBSCRIPTION,
      entityId = "#id"
   )
   public void softDeleteById(long id) {
      subscriptionMutationRepository.deleteById(id);
   }
}
