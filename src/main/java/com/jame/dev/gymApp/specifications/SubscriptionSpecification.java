package com.jame.dev.gymApp.specifications;

import com.jame.dev.gymApp.entity.*;
import jakarta.persistence.criteria.*;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

public class SubscriptionSpecification implements Specification<SubscriptionEntity> {

   private final String search;

   @Override
   public @Nullable Predicate toPredicate(Root<SubscriptionEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
      if (search == null || search.isBlank()) return cb.conjunction();

      final String like = "%" + search.toLowerCase() + "%";

      final Join<SubscriptionEntity, PricingEntity> ignoredpricingJoin = root.join("pricing", JoinType.INNER);
      final Join<PricingEntity, MemberShipEntity> membershipJoin = root.join("membership", JoinType.INNER);
      final Join<SubscriptionEntity, CustomerEntity> ignoredCustomerJoin = root.join("customer", JoinType.INNER);
      final Join<CustomerEntity, UserEntity> userJoin = root.join("user", JoinType.INNER);
      query.distinct(true);

      return cb.or(
         cb.like(cb.lower(membershipJoin.get("membership").as(String.class)), like),
         cb.like(cb.lower(userJoin.get("email")), like),
         cb.equal(root.get("finished"), Boolean.parseBoolean(like))
      );
   }

   public SubscriptionSpecification(String search) {
      this.search = search;
   }
}
