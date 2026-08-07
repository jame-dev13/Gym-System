package com.jame.dev.gymApp.features.audit.application.support.strategy.crud;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.application.model.UserBeforeUpdateModel;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogCrudPayload;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.crud.AuditLogCrudEntityResolver;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuditLogCrudUserResolver implements AuditLogCrudEntityResolver {
   @Override
   public AuditLogEntityType entity() {
      return AuditLogEntityType.USER;
   }

   @Override
   public AuditLogCrudPayload resolveUpdate(AuditExecutionContext ctx) {
      final var userRes = (UserResponse) ctx.getResultValue();
      final var userBeforeModel = (UserBeforeUpdateModel) ctx.getInput();
      return AuditLogCrudPayload.builder()
         .before(Map.of(
            "name", userBeforeModel.name(),
            "email", userBeforeModel.email(),
            "roles", userBeforeModel.roles()
         ))
         .after(Map.of(
            "name", userRes.name(),
            "email", userRes.email(),
            "roles", userRes.roles()
         ))
         .build();
   }
}
