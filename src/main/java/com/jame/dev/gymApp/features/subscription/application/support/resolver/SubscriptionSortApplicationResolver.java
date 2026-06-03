package com.jame.dev.gymApp.features.subscription.application.support.resolver;

import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionSortProperty;
import com.jame.dev.gymApp.infrastructure.sort.SortPropertyResolver;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("subSortAppResolver")
public class SubscriptionSortApplicationResolver implements SortPropertyResolver {
   private static final Map<String, String> PROPERTY_MAP = Arrays.stream(
      SubscriptionSortProperty.values()
   ).collect(Collectors.toMap(
      SubscriptionSortProperty::getApiProperty,
      SubscriptionSortProperty::getEntityProperty
   ));

   @Override
   public Pageable resolve(Pageable pageable) {
      if (pageable.getSort().isUnsorted()) return pageable;
      System.out.println(PROPERTY_MAP);
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
