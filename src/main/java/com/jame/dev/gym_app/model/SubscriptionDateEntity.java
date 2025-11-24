package com.jame.dev.gym_app.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "subscription_dates")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubscriptionDateEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(nullable = false)
   private Long id;

   @Column(name = "subscription_date", nullable = false)
   private LocalDate subscriptionDate;

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      SubscriptionDateEntity that = (SubscriptionDateEntity) o;
      return Objects.equals(id, that.id) && Objects.nonNull(that.id);
   }

   @Override
   public int hashCode() {
      return getClass().hashCode();
   }

   @Override
   public String toString() {
      return "SubscriptionDateEntity{" +
              "id=" + id +
              ", subscriptionDate=" + subscriptionDate +
              '}';
   }
}
