package com.jame.dev.gym_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "renew_dates")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RenewDateEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(nullable = false)
   private Long id;

   @Column(name = "start_date_id", nullable = false)
   private LocalDate startDateId;

   @OneToOne(fetch = FetchType.LAZY)
   private SubscriptionEntity subscription;
}
