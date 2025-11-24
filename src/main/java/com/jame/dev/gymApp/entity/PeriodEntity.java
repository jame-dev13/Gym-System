package com.jame.dev.gymApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "periods")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class PeriodEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(nullable = false)
   @Setter(AccessLevel.NONE)
   private Long id;

   @Column(name = "start_period", nullable = false)
   private LocalDate startPeriod;

   @Column(name = "end_period", nullable = false)
   private LocalDate endPeriod;
}
