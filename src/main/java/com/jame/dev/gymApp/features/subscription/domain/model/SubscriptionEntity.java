package com.jame.dev.gymApp.features.subscription.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jame.dev.gymApp.domain.model.BaseEntity;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
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
@SQLDelete(sql = """
   UPDATE subscriptions
   SET
      active = false,
      deleted_at = NOW(),
      status = 'DROPPED'
   WHERE id = ?
   """)
@DynamicUpdate
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
   @JoinColumn(name = "membership_id")
   @NonNull
   private MembershipEntity membership;

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
   @Builder.Default
   private List<PeriodEntity> subscriptionPeriods = new LinkedList<>();

   @OneToMany(
      mappedBy = "subscription",
      fetch = FetchType.LAZY,
      cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.REFRESH},
      orphanRemoval = true)
   @JsonIgnore
   @Builder.Default
   private List<PaymentEntity> payments = new LinkedList<>();

   @OneToMany(
      mappedBy = "subscription",
      fetch = FetchType.LAZY,
      cascade = {CascadeType.REMOVE, CascadeType.MERGE},
      orphanRemoval = true
   )
   @JsonIgnore
   @Builder.Default
   private List<SubscriberNotificationEntity> notifications = new LinkedList<>();

   @Column(name = "status", nullable = false, length = 12)
   @Enumerated(EnumType.STRING)
   private SubscriptionStatus status;

   @Override
   public String toString() {
      return """
         SubscriptionEntity{
             id=%d,
             customerId=%d,
             membership=%s,
             active=%b,
             status=%s
         }""".formatted(
         super.getId(),
         customer.getId(),
         membership.getMembership(),
         active,
         status.name()
      );
   }
}

