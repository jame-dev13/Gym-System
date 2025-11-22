package com.jame.dev.gym_app.model;

import com.jame.dev.gym_app.model.enums.Membership;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "memberships",
        indexes = @Index(name = "idx_memberships_membership",
                columnList = "membership",
                unique = true))
public class MemberShipEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(nullable = false)
   @Setter(AccessLevel.NONE)
   private Integer id;

   @Enumerated(EnumType.STRING)
   @Column(name = "membership", unique = true)
   private Membership membership;
}
