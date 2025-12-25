package com.jame.dev.gymApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedList;
import java.util.List;
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

   @OneToOne(fetch = FetchType.LAZY, optional = false)
   @NonNull
   private CustomerEntity customer;

   @OneToOne(fetch = FetchType.LAZY, optional = false)
   @NonNull
   private PricingEntity pricing;

   @OneToMany(fetch = FetchType.LAZY, cascade = {
           CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH
   })
   @JoinTable(name = "subscription_periods",
           joinColumns = @JoinColumn(name = "subscription_id"),
           inverseJoinColumns = @JoinColumn(name = "period_id"),
           indexes = @Index(name = "idx_subscription_period_unique", columnList = "period_id"))
   private List<PeriodEntity> subscriptionPeriods = new LinkedList<>();

   @Column(name = "active", nullable = false)
   @Setter(AccessLevel.NONE)
   private boolean active;

   @Column(name = "finished", nullable = false)
   private boolean finished;

   @PrePersist
   private void setFlags() {
      this.active = true;
      this.finished = false;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || o.getClass() != getClass()) return false;
      SubscriptionEntity that = (SubscriptionEntity) o;
      return Objects.nonNull(that.id) && (Objects.equals(that.id, id));
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
                  active=%b,
                  finished=%b
              }""".formatted(
              id,
              customer.getId(),
              pricing.getId(),
              active,
              finished
      );
   }
}
