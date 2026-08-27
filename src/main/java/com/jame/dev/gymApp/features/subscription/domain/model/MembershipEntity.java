package com.jame.dev.gymApp.features.subscription.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "memberships", indexes = {
        @Index(name = "idx_memberships_membership_unq", columnList = "membership", unique = true)
})
public class MembershipEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(nullable = false)
   @Setter(AccessLevel.NONE)
   private Integer id;

   @Enumerated(EnumType.STRING)
   @Column(name = "membership", unique = true, length = 15)
   @NonNull
   private Membership membership;

   @Column(name = "price", precision = 10, scale = 2, nullable = false)
   private BigDecimal price;

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || o.getClass() != getClass()) return false;
      MembershipEntity that = (MembershipEntity) o;
      return Objects.nonNull(that.id) && (Objects.equals(that
              .id, id));
   }

   @Override
   public int hashCode() {
      return getClass().hashCode();
   }
}
