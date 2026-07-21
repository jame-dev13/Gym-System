package com.jame.dev.gymApp.domain.model;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("active = true")
public abstract class BaseEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id", nullable = false)
   @Setter(AccessLevel.NONE)
   protected Long id;

   @Column(name = "created_at", updatable = false, nullable = false)
   @CreatedDate
   protected Instant createdAt;

   @Column(name = "updated_at")
   @LastModifiedDate
   protected Instant updatedAt;

   @Column(name = "deleted_at")
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
}
