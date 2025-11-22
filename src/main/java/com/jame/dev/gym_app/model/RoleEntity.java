package com.jame.dev.gym_app.model;

import com.jame.dev.gym_app.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
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
   private Role role;

}
