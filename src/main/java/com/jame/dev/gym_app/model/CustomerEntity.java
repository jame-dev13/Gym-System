package com.jame.dev.gym_app.model;


import com.jame.dev.gym_app.model.enums.MembershipStatus;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "customers")
public class CustomerEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(nullable = false)
   @Setter(AccessLevel.NONE)
   private Long id;

   @OneToOne(fetch = FetchType.LAZY)
   private UserEntity user;

   @OneToOne(fetch = FetchType.LAZY)
   private MemberShipEntity memberShipEntity;

   @Column(name = "active", nullable = false)
   @Setter(AccessLevel.NONE)
   private Boolean active;

   @Column(name = "membership_status", nullable = false)
   @Enumerated(EnumType.STRING)
   private MembershipStatus membershipStatus;
}
