package com.jame.dev.gymApp.features.audit.application.support.helper;

import com.jame.dev.gymApp.features.audit.application.dto.AuditLogActor;
import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.infrastructure.security.identity.IdentityExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ExtractAuditLogActorHelper {

   private final IdentityExtractorService identityExtractorService;

   public AuditLogActor extractLogActor(final AuditExecutionContext context) {
      if (Objects.nonNull(context.getAuditLogActor()) && context.getAnnotation().entityType() == AuditLogEntityType.AUTHENTICATION) {
         return context.getAuditLogActor();
      }

      final AuthPrincipal principal = identityExtractorService.getContextPrincipal();
      return new AuditLogActor(principal.id(), principal.username());
   }
}
