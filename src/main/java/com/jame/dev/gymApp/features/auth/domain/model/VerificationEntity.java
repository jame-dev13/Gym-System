package com.jame.dev.gymApp.features.auth.domain.model;

import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
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
      optional = false)
   @JoinColumn(
      name = "user_id",
      unique = true,
      foreignKey = @ForeignKey(
         name = "fk_verifications_user_id",
         foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
      )
   )
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
