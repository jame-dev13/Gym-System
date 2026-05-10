package com.jame.dev.gymApp.specifications;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import jakarta.persistence.criteria.*;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications implements Specification<UserEntity> {

   private final String search;

   @Override
   public @Nullable Predicate toPredicate(Root<UserEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
      if (search == null || search.isBlank()) {
         return cb.conjunction();
      }

      final String like = "%" + search.toLowerCase() + "%";

      final Join<UserEntity, RoleEntity> roleJoin = root.join("roles", JoinType.INNER);

      query.distinct(true);

      return cb.or(
         cb.like(cb.lower(root.get("name")), like),
         cb.like(cb.lower(root.get("email")), like),
         cb.equal(cb.upper(roleJoin.get("role").as(String.class)), search.toUpperCase())
      );
   }

   public UserSpecifications(String search) {
      this.search = search;
   }
}
