package com.jame.dev.gymApp.features.audit.application.support.strategy.state.after;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogActor;
import com.jame.dev.gymApp.features.audit.application.model.AuditAuthOperation;
import com.jame.dev.gymApp.features.audit.application.model.AuditAuthenticationResultValue;
import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.AuditAfterResolver;
import com.jame.dev.gymApp.features.auth.api.request.RegisterRequest;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.domain.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterAuditAfterResolver implements AuditAfterResolver {
   private final UserQueryRepository queryRepository;

   @Override
   public AuditLogAction action() {
      return AuditLogAction.REGISTER;
   }

   @Override
   public void resolve(AuditExecutionContext context) {
      final String email = ((RegisterRequest) context.getInput()).email();
      final Long id = queryRepository.findByEmail(email)
         .map(UserEntity::getId)
         .orElseThrow(() -> new NotFoundException("User not found for: " + email));
      context.setEntityId(id);
      context.setResultValue(new AuditAuthenticationResultValue(id, email, AuditAuthOperation.REGISTER.getOp()));
      context.setAuditLogActor(new AuditLogActor(id, email));
   }
}
