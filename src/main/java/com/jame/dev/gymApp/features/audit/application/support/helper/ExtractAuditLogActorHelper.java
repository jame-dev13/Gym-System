package com.jame.dev.gymApp.features.audit.application.support.helper;

import com.jame.dev.gymApp.features.audit.application.dto.AuditLogActor;
import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.infrastructure.security.identity.IdentityExtractorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExtractAuditLogActorHelper {

   private final IdentityExtractorService identityExtractorService;

   public AuditLogActor extractLogActor(final AuditExecutionContext context) {
      if (Objects.nonNull(context.getAuditLogActor()))
         return context.getAuditLogActor();
      try {
         final AuthPrincipal principal = identityExtractorService.getContextPrincipal();
         return new AuditLogActor(principal.id(), principal.username());
      } catch (final Exception ex) {
         log.error("Exception extracting audit actor.", ex);
         return new AuditLogActor(-1L, "ANONYMOUS");
      }
   }
}
