package com.jame.dev.gymApp.entity;

import com.jame.dev.gymApp.shared.enums.Membership;
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

   @Override
   public boolean equals(Object o) {
      if(this == o) return true;
      if(o == null || o.getClass() != getClass()) return false;
      MemberShipEntity that = (MemberShipEntity) o;
      return Objects.nonNull(that.id) && (Objects.equals(that
              .id, id));
   }

   @Override
   public int hashCode() {
      return getClass().hashCode();
   }
}
