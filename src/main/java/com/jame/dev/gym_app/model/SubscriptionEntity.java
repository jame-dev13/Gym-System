package com.jame.dev.gym_app.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class SubscriptionEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(nullable = false)
   @Setter(AccessLevel.NONE)
   private Long id;

   @OneToOne(fetch = FetchType.LAZY)
   private CustomerEntity customerEntity;

   @OneToOne(fetch = FetchType.LAZY)
   private MembershipPricing membershipPricing;
}
