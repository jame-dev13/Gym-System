package com.jame.dev.gymApp.entity;

import com.jame.dev.gymApp.shared.enums.Role;
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
@Table(name = "roles", indexes = {
        @Index(name = "idx_roles_role_unq", columnList = "role", unique = true)
})
public class RoleEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Setter(AccessLevel.NONE)
   private Integer id;

   @Column(name = "role", nullable = false, unique = true)
   @Enumerated(EnumType.STRING)
   @NonNull
   private Role role;


   @Override
   public boolean equals(Object o){
      if(this == o) return true;
      if(o == null || o.getClass() != getClass()) return false;
      RoleEntity that = (RoleEntity) o;
      return Objects.nonNull(that.id) && (Objects.equals(that
              .id, id));
   }

   @Override
   public int hashCode() {
      return getClass().hashCode();
   }
}
