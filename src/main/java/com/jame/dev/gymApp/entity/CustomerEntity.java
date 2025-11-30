package com.jame.dev.gymApp.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

   @OneToOne(fetch = FetchType.LAZY, optional = false)
   @ToString.Exclude
   @NonNull
   private UserEntity user;

   @NotBlank
   @NonNull
   @Column(name = "contact",length = 15, nullable = false)
   private String phoneContact;

   @Column(name = "active", nullable = false)
   @Setter(AccessLevel.NONE)
   @NonNull
   private Boolean active;

   @PrePersist
   private void setActive(){
      this.active = Boolean.TRUE;
   }

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
