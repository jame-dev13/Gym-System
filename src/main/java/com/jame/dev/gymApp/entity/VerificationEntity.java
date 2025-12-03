package com.jame.dev.gymApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "tokens")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class VerificationEntity {
   @Id
   @Column(nullable = false, length = 10, unique = true)
   private String id;

   @OneToOne(fetch = FetchType.LAZY, optional = false)
   @NonNull
   @ToString.Exclude
   private UserEntity user;

   @Column(name = "expires_at", nullable = false)
   private Instant expiration;

   @Column(name = "verified", nullable = false)
   private boolean verified;

   @Override
   public boolean equals(final Object o){
      if(this == o) return true;
      if(o == null || o.getClass() != this.getClass()) return false;
      final VerificationEntity that = (VerificationEntity) o;
      return (that.id != null) && (that.id.equals(this.id));
   }

   @Override
   public int hashCode(){
      return getClass().hashCode();
   }
}
