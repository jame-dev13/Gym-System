package com.jame.dev.gymApp.features.customer.application.service.mutation;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.auth.infrastructure.auth.AuthenticationUserResolver;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.DeleteCurrentCustomerUseCase;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
import com.jame.dev.gymApp.features.customer.infrastructure.annotations.EvictOnDropCustomers;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class DeleteCurrentCustomerUseCaseService implements DeleteCurrentCustomerUseCase {
   private final CustomerMutationRepository mutationRepository;
   private final AuthenticationUserResolver authenticationUserResolver;

   @Override
   @EvictOnDropCustomers
   @AuditLog(
      entityType = AuditLogEntityType.CUSTOMER,
      action = AuditLogAction.DELETE,
      input = "#authentication"
   )
   public void deleteCurrent(Authentication authentication) {
      final long userId = authenticationUserResolver.resolveUserId(authentication);
      mutationRepository.deleteByUserId(userId);
   }
}
