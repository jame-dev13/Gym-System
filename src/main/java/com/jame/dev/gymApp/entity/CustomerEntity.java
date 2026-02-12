package com.jame.dev.gymApp.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "customers")
@SQLDelete(sql = "UPDATE customers SET active = false WHERE id = ?")
public class CustomerEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(nullable = false)
   @Setter(AccessLevel.NONE)
   private Long id;

   @OneToOne(fetch = FetchType.LAZY, optional = false,
           cascade = {CascadeType.REFRESH, CascadeType.MERGE})
   @JoinColumn(name = "user_id", nullable = false, unique = true)
   @ToString.Exclude
   @NonNull
   private UserEntity user;

   @Nullable
   @Column(name = "contact", length = 15)
   private String phoneContact;

   @Column(name = "active", nullable = false)
   private boolean active;

   @PrePersist
   private void setActive() {
      this.active = Boolean.TRUE;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || o.getClass() != getClass()) return false;
      CustomerEntity that = (CustomerEntity) o;
      return Objects.nonNull(that.id) && (Objects.equals(that
              .id, id));
   }

   @Override
   public int hashCode() {
      return getClass().hashCode();
   }
}
