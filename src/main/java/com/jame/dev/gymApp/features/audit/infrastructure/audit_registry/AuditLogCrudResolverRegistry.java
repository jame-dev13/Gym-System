package com.jame.dev.gymApp.features.audit.infrastructure.audit_registry;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.crud.AuditLogCrudEntityResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AuditLogCrudResolverRegistry {

   private final Map<AuditLogEntityType, AuditLogCrudEntityResolver> resolver;

   public AuditLogCrudResolverRegistry(final List<AuditLogCrudEntityResolver> resolver) {
      this.resolver = resolver.stream()
         .collect(
            Collectors.toUnmodifiableMap(
               AuditLogCrudEntityResolver::entity,
               Function.identity()
            ));
   }

   public AuditLogCrudEntityResolver check(final AuditLogEntityType type) {
      return Optional
         .of(resolver.get(type))
         .orElseThrow(() -> new IllegalArgumentException("Unknown AuditLogEntityType " + type));
   }
}
