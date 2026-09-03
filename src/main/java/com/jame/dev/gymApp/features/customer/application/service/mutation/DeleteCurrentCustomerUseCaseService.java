package com.jame.dev.gymApp.features.customer.application.service.mutation;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.DeleteCurrentCustomerUseCase;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
import com.jame.dev.gymApp.features.customer.infrastructure.annotations.EvictCurrentOnDeleteCustomer;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class DeleteCurrentCustomerUseCaseService implements DeleteCurrentCustomerUseCase {
   private final CustomerMutationRepository mutationRepository;

   @Override
   @Transactional
   @EvictCurrentOnDeleteCustomer
   @AuditLog(
      entityType = AuditLogEntityType.CUSTOMER,
      action = AuditLogAction.DELETE,
      input = "#principal"
   )
   public void deleteCurrent(AuthPrincipal principal) {
      mutationRepository.deleteById(principal.id());
   }
}
