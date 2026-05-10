package com.jame.dev.gymApp.validators;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public class PageableValidator {
   private static final Set<String> WHITE_LIST = Set.of(
      "id", "name", "email",
      "userEmail", "pricing", "membership"
   );

   public static Pageable checkSort(Pageable pageable) {
      final Sort sort = pageable.getSort();
      for (Sort.Order order : sort) {
         final String property = order.getProperty();
         if(!WHITE_LIST.contains(property)) {
            throw new IllegalArgumentException("Cannot sort by property: " + property);
         }
      }
      return pageable;
   }
}
