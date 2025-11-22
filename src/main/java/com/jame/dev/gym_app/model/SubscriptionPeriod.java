package com.jame.dev.gym_app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "subscription_period",
        indexes = {
                @Index(name = "idx_subscription_period_unq", columnList = "start_period", unique = true)
        })
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SubscriptionPeriod {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(nullable = false)
   @Setter(AccessLevel.NONE)
   private Long id;

   @OneToOne(fetch = FetchType.LAZY)
   private SubscriptionEntity subscriptionEntity;

   @Column(name = "start_period", nullable = false, unique = true)
   private LocalDate startPeriod;

   @Column(name = "end_period", nullable = false)
   private LocalDate endPeriod;
}
