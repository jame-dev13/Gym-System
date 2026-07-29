package com.jame.dev.gymApp.features.backup.infrastructure.sort;

import com.jame.dev.gymApp.infrastructure.sort.SortPropertyResolver;
import com.jame.dev.gymApp.infrastructure.sort.SortResolver;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;

@Component("backupSortPropertyResolver")
public class BackupSortPropertyResolver implements SortPropertyResolver {
   private final Map<String, String> properties = EnumSet.allOf(BackupSortProperty.class)
      .stream()
      .collect(Collectors.toMap(
         BackupSortProperty::getApiProperty,
         BackupSortProperty::getEntityProperty
      ));

   @Override
   public Pageable resolve(Pageable pageable) {
      return SortResolver.resolveSortPropertiesFrom(pageable, properties);
   }
}
