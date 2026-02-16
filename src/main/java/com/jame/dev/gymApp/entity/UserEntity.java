package com.jame.dev.gymApp.entity;

import com.jame.dev.gymApp.shared.enums.AuthProvider;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email_unq", columnList = "email", unique = true),
        @Index(name = "idx_users_pagination", columnList = "id, active")
})
@SQLDelete(sql = "UPDATE users SET active = false, deleted_at = NOW() WHERE id = ?", table = "users")
public class UserEntity extends BaseEntity {
   @OneToOne(
           mappedBy = "user",
           cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.REMOVE},
           orphanRemoval = true
   )
   @Nullable
   private CustomerEntity customer;

   @NotBlank
   @Column(name = "name", length = 150, nullable = false)
   private String name;

   @Email
   @Column(name = "email", length = 120, nullable = false, unique = true)
   private String email;

   @Column(name = "password")
   @ToString.Exclude
   private String password;

   @Enumerated(EnumType.STRING)
   @Column(name = "auth_provider", length = 15, nullable = false)
   private AuthProvider provider;

   @EqualsAndHashCode.Exclude
   @ToString.Exclude
   @ManyToMany(fetch = FetchType.EAGER,
           cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
   @JoinTable(name = "user_roles",
           joinColumns = @JoinColumn(name = "user_id"),
           inverseJoinColumns = @JoinColumn(name = "role_id"))
   private Set<RoleEntity> roles = new HashSet<>();
}
