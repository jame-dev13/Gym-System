package com.jame.dev.gymApp.features.subscription.domain.model;

import com.jame.dev.gymApp.domain.model.BaseEntity;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "payments", indexes = {
   @Index(name = "idx_payment_stripe_session_id", columnList = "stripe_session_id"),
   @Index(name = "idx_payment_customer_id", columnList = "customer_id"),
   @Index(name= "idx_payment_created_at", columnList = "created_at")
})
@SQLDelete(sql = "UPDATE payments SET active = false, deleted_at = NOW() WHERE id = ?")
public class PaymentEntity extends BaseEntity {

   @Column(name = "stripe_session_id", nullable = false)
   @NonNull
   private String stripeSessionId;

   @Column(name = "stripe_payment_intent_id")
   private String stripePaymentIntentId;

   @Column(name = "stripe_subscription_id")
   private String stripeSubscriptionId;

   @Column(name = "amount", nullable = false, precision = 10, scale = 2)
   @NonNull
   private BigDecimal amount;

   @Column(name = "currency", nullable = false, length = 6)
   @NonNull
   private String currency;

   @Enumerated(EnumType.STRING)
   @Column(name = "status", nullable = false, length = 12)
   @NonNull
   private PaymentStatus status;

   @Enumerated(EnumType.STRING)
   @Column(name = "payment_method", nullable = false, length = 12)
   @NonNull
   private PaymentMethod paymentMethod;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(
      name = "subscription_id",
      nullable = false,
      foreignKey = @ForeignKey(
         name = "fk_payment_subscription_id",
         foreignKeyDefinition = "FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE CASCADE"
      )
   )
   @NonNull
   private SubscriptionEntity subscription;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(
      name = "customer_id",
      nullable = false,
      foreignKey = @ForeignKey(
         name = "fk_payment_customer_id",
         foreignKeyDefinition = "FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE"
      )
   )
   @NonNull
   private CustomerEntity customer;
}
