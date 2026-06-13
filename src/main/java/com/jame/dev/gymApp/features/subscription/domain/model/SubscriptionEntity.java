package com.jame.dev.gymApp.features.subscription.domain.model;

import com.jame.dev.gymApp.domain.model.BaseEntity;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

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
public class SubscriptionEntity extends BaseEntity {

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(
      name = "customer_id",
      nullable = false,
      foreignKey = @ForeignKey(
         name = "fk_subscription_customer_id",
         foreignKeyDefinition = "FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE"
      )
   )
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
      indexes = @Index(name = "idx_subscription_period_unique", columnList = "period_id"),
      foreignKey = @ForeignKey(
         name = "fk_subscription_period_id",
         foreignKeyDefinition = "FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE CASCADE"
      )
   )
   private List<PeriodEntity> subscriptionPeriods = new LinkedList<>();

   @Column(name = "finished", nullable = false)
   private boolean finished;

   @Column(name = "paid", nullable = false)
   private boolean paid = false;

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
             isPaid=%b
         }""".formatted(
         super.getId(),
         customer.getId(),
         pricing.getId(),
         active,
         finished,
         paid
      );
   }
}

