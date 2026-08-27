package com.jame.dev.gymApp.features.subscription.infrastructure.specification;

import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.MembershipEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionStatus;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import jakarta.persistence.criteria.*;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionSpecification implements Specification<SubscriptionEntity> {

   private final String search;

   @Override
   public @Nullable Predicate toPredicate(Root<SubscriptionEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
      if (search == null || search.isBlank()) return cb.conjunction();

      final String like = "%" + search.toLowerCase() + "%";
      final String trimmed = search.trim();

      final Join<SubscriptionEntity, MembershipEntity> membershipJoin = root.join("membership", JoinType.LEFT);
      final Join<SubscriptionEntity, CustomerEntity> customerJoin = root.join("customer", JoinType.LEFT);
      final Join<CustomerEntity, UserEntity> userJoin = customerJoin.join("user", JoinType.LEFT);

      query.distinct(true);

      final List<Predicate> predicates = new ArrayList<>();
      predicates.add(cb.like(cb.lower(userJoin.get("email")), like));

      predicates.add(cb.equal(
         cb.lower(membershipJoin.get("membership")).as(String.class),
         trimmed.toLowerCase()
      ));

      try {
         predicates.add(cb.equal(
            root.get("status").as(SubscriptionStatus.class),
            SubscriptionStatus.valueOf(trimmed.toUpperCase())
         ));
      } catch (IllegalArgumentException ignored) {
      }

      try {
         BigDecimal price = new BigDecimal(trimmed);
         predicates.add(cb.equal(
            membershipJoin.get("price").as(BigDecimal.class),
            price
         ));
      } catch (NumberFormatException ignored) {
      }

      return cb.or(predicates.toArray(new Predicate[0]));
   }

   public SubscriptionSpecification(String search) {
      this.search = search;
   }
}
