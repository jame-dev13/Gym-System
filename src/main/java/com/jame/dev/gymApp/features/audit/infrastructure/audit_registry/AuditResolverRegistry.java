package com.jame.dev.gymApp.features.audit.infrastructure.audit_registry;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.AuditAfterResolver;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.AuditBeforeResolver;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;

@Component
public class AuditResolverRegistry {

   private final EnumMap<AuditLogAction, AuditBeforeResolver> before;
   private final EnumMap<AuditLogAction, AuditAfterResolver> after;

   public AuditResolverRegistry(
      final List<AuditBeforeResolver> beforeResolvers,
      final List<AuditAfterResolver> afterResolvers
   ) {
      this.before = new EnumMap<>(AuditLogAction.class);
      this.after = new EnumMap<>(AuditLogAction.class);

      beforeResolvers.forEach(
         (resolver) -> this.before.put(resolver.action(), resolver)
      );

      afterResolvers.forEach(
         resolver -> this.after.put(resolver.action(), resolver)
      );
   }

   public AuditBeforeResolver before(AuditLogAction action) {
      return before.get(action);
   }

   public AuditAfterResolver after(AuditLogAction action) {
      return after.get(action);
   }

   public boolean checkAfterRegistry(AuditLogAction action) {
      return after.containsKey(action);
   }
}
