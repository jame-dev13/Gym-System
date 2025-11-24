package com.jame.dev.gymApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "subscriptions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SubscriptionEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(nullable = false)
   @Setter(AccessLevel.NONE)
   private Long id;

   @OneToOne(fetch = FetchType.LAZY)
   private CustomerEntity customer;

   @OneToOne(fetch = FetchType.LAZY)
   private PricingEntity pricing;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "period_id")
   private PeriodEntity period;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "subscription_date_id")
   private SubscriptionDateEntity subscriptionDate;

   @Column(name = "active", nullable = false)
   @Setter(AccessLevel.NONE)
   private Boolean active;

   @Column(name = "finished", nullable = false)
   private Boolean finished;

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || o.getClass() != getClass()) return false;
      SubscriptionEntity that = (SubscriptionEntity) o;
      return Objects.nonNull(that.id) && (Objects.equals(that
              .id, id));
   }

   @Override
   public int hashCode() {
      return getClass().hashCode();
   }

   @Override
   public String toString() {
      return """
              SubscriptionEntity{
                  id=%d,
                  customerId=%d,
                  pricingId=%d,
                  periodId=%d,
                  subscriptionDateId=%d,
                  active=%b,
                  finished=%b
              }""".formatted(
              id,
              customer.getId(),
              pricing.getId(),
              period.getId(),
              subscriptionDate.getId(),
              active,
              finished
      );
   }

}
