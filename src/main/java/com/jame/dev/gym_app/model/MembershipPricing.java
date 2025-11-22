package com.jame.dev.gym_app.model;

import com.jame.dev.gym_app.model.enums.Membership;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "membership_pricing")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MembershipPricing {
   @Id
   @Enumerated(EnumType.STRING)
   @EqualsAndHashCode.Include
   @Column(name = "membership_id", nullable = false, unique = true)
   private Membership membership;

   @Column(name = "price", precision = 10, scale = 2, nullable = false)
   private BigDecimal price;

}
