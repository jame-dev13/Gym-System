package com.jame.dev.gymApp.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
@FilterDef(name = "deletedFilter", parameters = @ParamDef(name = "active", type = Boolean.class))
@Filter(name = "deletedFilter", condition = "active = :active")
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

}
