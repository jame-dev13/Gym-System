package com.jame.dev.gymApp.features.notification.domain.model;


import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "subscriber_notifications")
public class SubscriberNotificationEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   @Column(name = "id", updatable = false, nullable = false, unique = true)
   private UUID id;

   @OneToOne(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.MERGE, CascadeType.REMOVE},
      optional = false)
   @JoinColumn(
      name = "subscription_id",
      nullable = false,
      unique = true,
      foreignKey = @ForeignKey(
         name = "fk_subscription_notication",
         foreignKeyDefinition = "FOREIGN KEY (subscription_id) REFERENCES subscriptions (id) ON DELETE CASCADE"
      )
   )
   @NonNull
   private SubscriptionEntity subscription;

   @Column(name = "range_notification_days")
   @Builder.Default
   @Min(value = 1, message = "Minium value allowed is 1.")
   @Max(value = 7,message = "Maximum value allowed is 7.")
   private int rangeNotificationDays = 7;

   @Column(name = "next_notifiaction_date", nullable = false)
   private LocalDateTime nextNotificationDate;

   @Column(name = "last_notification_date")
   private LocalDateTime lastNotificationDate;

}
