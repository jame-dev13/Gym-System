package com.jame.dev.gymApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Table(name = "tokens", indexes = {
        @Index(name = "idx_verification_user_unq", columnList = "user_id", unique = true)
})
public class VerificationEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Setter(AccessLevel.NONE)
   private Long id;

   @OneToOne(
           fetch = FetchType.LAZY,
           optional = false,
           cascade = {CascadeType.REFRESH, CascadeType.MERGE})
   @JoinColumn(name = "user_id")
   @NonNull
   private UserEntity user;

   @Column(name = "token", nullable = false)
   private String token;

   @Column(name = "expires_at", nullable = false)
   private Instant expiration;

   @Column(name = "verified", nullable = false)
   private boolean verified;

   @Override
   public boolean equals(final Object o) {
      if (this == o) return true;
      if (o == null || o.getClass() != this.getClass()) return false;
      final VerificationEntity that = (VerificationEntity) o;
      return (that.id != null) && (that.id.equals(this.id));
   }

   @Override
   public int hashCode() {
      return getClass().hashCode();
   }
}
