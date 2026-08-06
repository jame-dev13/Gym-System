package com.jame.dev.gymApp.features.audit.infrastructure.sort.resolver;

import com.jame.dev.gymApp.features.audit.infrastructure.sort.model.AuditLogSortProperty;
import com.jame.dev.gymApp.infrastructure.sort.SortPropertyResolver;
import com.jame.dev.gymApp.infrastructure.sort.SortResolver;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;

@Component("auditLogSortAppResolver")
public class AuditLogSortApplicationResolver implements SortPropertyResolver {
   private static final Map<String, String> PROPERTY_MAP = EnumSet.allOf(AuditLogSortProperty.class)
      .stream()
      .collect(Collectors.toMap(
         AuditLogSortProperty::getApiProperty,
         AuditLogSortProperty::getEntityProperty));

   @Override
   public Pageable resolve(Pageable pageable) {
      return SortResolver.resolveSortPropertiesFrom(pageable, PROPERTY_MAP);
   }
}
