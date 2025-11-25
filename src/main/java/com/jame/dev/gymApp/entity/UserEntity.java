package com.jame.dev.gymApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email_unq", columnList = "email", unique = true)
})
public class UserEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(nullable = false)
   @Setter(AccessLevel.NONE)
   private Long id;

   @NotBlank
   @Column(name = "name", length = 150, nullable = false)
   private String name;

   @Email
   @Column(name = "email", length = 120, nullable = false, unique = true)
   private String email;

   @NotBlank
   @Column(name = "password", nullable = false)
   private String password;

   @EqualsAndHashCode.Exclude
   @ToString.Exclude
   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(name = "user_roles",
           joinColumns = @JoinColumn(name = "user_id"),
           inverseJoinColumns = @JoinColumn(name = "role_id"))
   private Set<RoleEntity> roles = new HashSet<>();

   @Column(name = "active", nullable = false)
   @Setter(AccessLevel.NONE)
   private Boolean active;

   @PrePersist
   private void setActive(){
      this.active = Boolean.TRUE;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if(o == null || getClass() != o.getClass()) return false;
      UserEntity that = (UserEntity) o;
      return Objects.nonNull(that.id) && (Objects.equals(that
              .id, id));
   }

   @Override
   public int hashCode() {
      return getClass().hashCode();
   }
}
