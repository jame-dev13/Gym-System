package com.jame.dev.gymApp.features.user.application.support.resolver;

import com.jame.dev.gymApp.features.user.domain.model.UserSortProperty;
import com.jame.dev.gymApp.infrastructure.sort.SortPropertyResolver;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("userSortApplicationResolver")
public class UserSortApplicationResolver implements SortPropertyResolver {
   private static final Map<String, String> PROPERTY_MAP = Arrays.stream(UserSortProperty.values())
      .collect(Collectors.toMap(
         UserSortProperty::getApiProperty,
         UserSortProperty::getEntityProperty));

   @Override
   public Pageable resolve(Pageable pageable) {
      if (pageable.getSort().isUnsorted())
         return pageable;

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
