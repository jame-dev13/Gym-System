package com.jame.dev.gymApp.features.audit.infrastructure.audit_registry;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.payload.AuditLogPayloadResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AuditLogPayloadResolverRegistry {

   private final Map<AuditLogAction, AuditLogPayloadResolver> resolver;

   public AuditLogPayloadResolverRegistry(final List<AuditLogPayloadResolver> resolver) {
      this.resolver = resolver.stream()
         .collect(
            Collectors.toUnmodifiableMap(
               AuditLogPayloadResolver::action,
               Function.identity()
            ));
   }


   public AuditLogPayloadResolver check(final AuditLogAction action) {
      return Optional
         .ofNullable(resolver.get(action))
         .orElseThrow(() -> new IllegalArgumentException("Unknown AuditLogAction " + action));
   }
}
