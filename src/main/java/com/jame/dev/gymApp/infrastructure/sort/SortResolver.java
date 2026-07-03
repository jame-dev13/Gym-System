package com.jame.dev.gymApp.infrastructure.sort;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

public class SortResolver {

   public static Pageable resolveSortPropertiesFrom(
      final Pageable pageable,
      final Map<String, String> PROPERTY_MAP
   ) {
      if (pageable.getSort().isUnsorted()) return pageable;
      final List<Sort.Order> resolverOrders = pageable.getSort()
         .stream()
         .map(o -> {
            final String property = PROPERTY_MAP.getOrDefault(o.getProperty(), null);
            if (property == null)
               throw new IllegalArgumentException("Sort property not allowed.");
            return Sort.Order
               .by(property)
               .with(o.getDirection())
               .with(o.getNullHandling());
         })
         .toList();

      return PageRequest.of(
         pageable.getPageNumber(),
         pageable.getPageSize(),
         Sort.by(resolverOrders)
      );
   }
}
