package com.jame.dev.gymApp.domain.model;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
@SQLRestriction("active = true")
public abstract class BaseEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id", nullable = false)
   @Setter(AccessLevel.NONE)
   protected Long id;

   @Column(name = "created_at", updatable = false)
   @Nullable
   protected Instant createdAt;

   @Column(name = "updated_at")
   @Nullable
   protected Instant updatedAt;

   @Column(name = "deleted_at")
   @Nullable
   protected Instant deletedAt;

   @Column(name = "active", nullable = false)
   protected boolean active = true;

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || o.getClass() != getClass()) return false;
      final BaseEntity that = (BaseEntity) o;
      return id != null && id.equals(that.id);
   }

   @Override
   public int hashCode() {
      return getClass().hashCode();
   }

   @PrePersist
   public void setCreatedAt() {
      this.createdAt = Instant.now();
   }

   @PreRemove
   public void setDeletedAt() {
      this.deletedAt = Instant.now();
   }
}
