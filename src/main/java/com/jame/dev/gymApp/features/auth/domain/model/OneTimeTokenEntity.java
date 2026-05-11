package com.jame.dev.gymApp.features.auth.domain.model;

import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "one_time_tokens", indexes = {
   @Index(name = "idx_one_time_tokens_user_unq", columnList = "user_id", unique = true)
})
public class OneTimeTokenEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Setter(AccessLevel.NONE)
   private Long id;

   @ManyToOne(
      fetch = FetchType.LAZY,
      optional = false)
   @JoinColumn(
      name = "user_id",
      unique = true,
      foreignKey = @ForeignKey(
         name = "fk_one_time_token_user_id",
         foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
      )
   )
   @NonNull
   private UserEntity user;

   @Column(name = "hash_token", nullable = false)
   private String hashToken;

   @Column(name = "token_verified", nullable = false)
   private boolean tokenVerified = false;

   @Column(name = "created_at", nullable = false)
   private Instant createdAt;

   @Column(name = "expires_at", nullable = false)
   private Instant expiresAt;

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      final OneTimeTokenEntity that = (OneTimeTokenEntity) o;
      return Objects.equals(id, that.id);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @PrePersist
   void setTimeStamps() {
      this.createdAt = Instant.now();
   }
}
