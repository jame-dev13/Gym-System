package com.jame.dev.gymApp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.LinkedList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "subscriptions", indexes = {
        @Index(name = "idx_subscription_customer_id", columnList = "customer_id"),
        @Index(name = "idx_subscriptions_pagination", columnList = "id, active")
})
@SQLDelete(sql = "UPDATE subscriptions SET active = false, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("active = true")
public class SubscriptionEntity extends  BaseEntity {

   @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
   @JoinColumn(name = "customer_id")
   @NotFound(action = NotFoundAction.IGNORE)
   @NonNull
   private CustomerEntity customer;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "pricing_id")
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

   @Column(name = "finished", nullable = false)
   private boolean finished;

   @PostPersist
   private void setStatus() {
      this.finished = false;
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
              super.getId(),
              customer.getId(),
              pricing.getId(),
              active,
              finished
      );
   }
}

