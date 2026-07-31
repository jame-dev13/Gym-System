package com.jame.dev.gymApp.features.notification.domain.model;


import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Table(name = "subscriber_notifications")
public class SubscriberNotificationEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   @Column(name = "id", updatable = false, nullable = false, unique = true)
   @Setter(AccessLevel.NONE)
   private UUID id;

   @ManyToOne(
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
   @ToString.Exclude
   private SubscriptionEntity subscription;

   @Column(name = "range_notification_days")
   @Builder.Default
   private int rangeNotificationDays = 7;

   @Column(name = "next_notifiaction_date")
   @Nullable
   private LocalDateTime nextNotificationDate;

   @Override
   public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      SubscriberNotificationEntity that = (SubscriberNotificationEntity) o;
      return Objects.equals(id, that.id);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }
}
