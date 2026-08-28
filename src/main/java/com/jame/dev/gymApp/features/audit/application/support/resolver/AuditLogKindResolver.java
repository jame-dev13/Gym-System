package com.jame.dev.gymApp.features.audit.application.support.resolver;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogKind;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

import static com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType.*;

@Component
public class AuditLogKindResolver {

   private final EnumSet<AuditLogEntityType> SYSTEM_KIND_SET = EnumSet.of(USER, CUSTOMER, SUBSCRIPTION);

   public void resolveKindForContext(final AuditExecutionContext context) {
      final var entityType = context.getAnnotation().entityType();
      if (SYSTEM_KIND_SET.contains(entityType) && context.getTh() == null) {
         context.setKind(AuditLogKind.SYSTEM);
      } else if (entityType == AUTHENTICATION && context.getTh() == null) {
         context.setKind(AuditLogKind.AUTH);
      } else if (context.getTh() != null) {
         context.setKind(AuditLogKind.ERROR);
      } else context.setKind(AuditLogKind.EXTERNAL);
   }
}
