package com.jame.dev.gymApp.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "customers")
public class CustomerEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(nullable = false)
   @Setter(AccessLevel.NONE)
   private Long id;

   @OneToOne(fetch = FetchType.LAZY)
   @ToString.Exclude
   private UserEntity user;

   @Column(name = "active", nullable = false)
   @Setter(AccessLevel.NONE)
   private Boolean active;


   @Override
   public boolean equals(Object o){
      if(this == o) return true;
      if(o == null || o.getClass() != getClass()) return false;
      CustomerEntity that = (CustomerEntity) o;
      return Objects.nonNull(that.id) && (Objects.equals(that
              .id, id));
   }

   @Override
   public int hashCode(){
      return getClass().hashCode();
   }
}
