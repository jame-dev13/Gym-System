package com.jame.dev.gymApp.entity;

import com.jame.dev.gymApp.shared.enums.Period;
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

   @Enumerated(EnumType.STRING)
   @Column(name = "period", nullable = false)
   private Period period;

   @Column(name = "start_period", nullable = false)
   private LocalDate startPeriod;

   @Column(name = "end_period", nullable = false)
   private LocalDate endPeriod;

   @PrePersist
   @PreUpdate
   private void setPeriodDates(){
      this.startPeriod = LocalDate.now();
      this.endPeriod = switch (period){
         case FORTNIGHTLY -> startPeriod.plusDays(15);
         case MONTHLY -> startPeriod.plusMonths(1);
         case QUARTERLY -> startPeriod.plusMonths(3);
         case ANNUAL -> startPeriod.plusYears(1);
         default -> throw new RuntimeException("Unknow value for " + period);
      };
   }
}
