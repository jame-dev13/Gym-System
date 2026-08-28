package com.jame.dev.gymApp.features.audit.infrastructure.audit_registry;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.entity.AuditActionBindResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AuditLogActionBindResolverRegistry {
   private final Map<AuditLogAction, AuditActionBindResolver> auditActionBindResolvers;

   public AuditLogActionBindResolverRegistry(List<AuditActionBindResolver> auditActionBindResolversList) {
      this.auditActionBindResolvers = auditActionBindResolversList
         .stream()
         .collect(
            Collectors.toUnmodifiableMap(
               AuditActionBindResolver::action,
               Function.identity()
            )
         );
   }

   public boolean existsByAction(final AuditLogAction action) {
      return auditActionBindResolvers.containsKey(action);
   }

   public AuditActionBindResolver getBinder(final AuditLogAction action) {
      return auditActionBindResolvers.get(action);
   }
}
