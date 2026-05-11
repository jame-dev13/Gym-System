package com.jame.dev.gymApp.features.customer.infrastructure.specification;

import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import jakarta.persistence.criteria.*;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecification implements Specification<CustomerEntity> {
   private final String search;

   @Override
   public @Nullable Predicate toPredicate(Root<CustomerEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
      if (search == null || search.isBlank()) return cb.conjunction();

      final String like = "%" + search.toLowerCase() + "%";

      final Join<CustomerEntity, UserEntity> userJoin = root.join("user", JoinType.LEFT);
      query.distinct(true);

      return cb.or(
        cb.like(cb.lower(userJoin.get("email")), like)
      );
   }

   public CustomerSpecification(String search) {
      this.search = search;
   }
}
