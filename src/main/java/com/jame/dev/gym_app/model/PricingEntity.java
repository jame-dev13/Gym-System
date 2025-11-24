package com.jame.dev.gym_app.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "membership_pricing")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class PricingEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(nullable = false)
   private Integer id;

   @OneToOne(fetch = FetchType.LAZY)
   @ToString.Exclude
   private MemberShipEntity memberShipEntity;

   @Column(name = "price", nullable = false, precision = 10, scale = 2)
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
