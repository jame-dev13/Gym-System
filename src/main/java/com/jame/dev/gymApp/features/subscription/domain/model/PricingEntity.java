package com.jame.dev.gymApp.features.subscription.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Deprecated
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Table(name = "membership_pricing", indexes = {
        @Index(name = "idx_membership_id_unq", columnList = "membership_id", unique = true)
})
public class PricingEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(nullable = false)
   private Integer id;

   @OneToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "membership_id")
   @ToString.Exclude
   @NonNull
   private MembershipEntity memberShipEntity;

   @Column(name = "price", nullable = false, precision = 10, scale = 2)
   @JoinColumn(name = "price_id")
   @NonNull
   private BigDecimal price;


   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || o.getClass() != getClass()) return false;
      PricingEntity that = (PricingEntity) o;
      return Objects.nonNull(that.id) && (Objects.equals(that
              .id, id));
   }

   public int hashCode() {
      return getClass().hashCode();
   }
}
