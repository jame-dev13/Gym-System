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
public class PeriodEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(nullable = false)
   @Setter(AccessLevel.NONE)
   private Long id;

   @Enumerated(EnumType.STRING)
   @Column(name = "period", nullable = false)
   @NonNull
   private Period period;

   @Column(name = "start_period", nullable = false)
   private LocalDate startPeriod;

   @Column(name = "end_period", nullable = false)
   @NonNull
   private LocalDate endPeriod;

   @Builder
   public PeriodEntity(final @NonNull Period period, final LocalDate startPeriod){
      this.period = period;
      this.startPeriod = startPeriod;
   }

   @PrePersist
   @PreUpdate
   private void setPeriodDates(){
      this.endPeriod = switch (period){
         case BIWEEKLY -> startPeriod.plusDays(15);
         case MONTHLY -> startPeriod.plusMonths(1);
         case QUARTERLY -> startPeriod.plusMonths(3);
         case ANNUAL -> startPeriod.plusYears(1);
      };
   }
}
