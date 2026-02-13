package com.jame.dev.gymApp.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.jspecify.annotations.Nullable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_user_id_unq", columnList = "user_id", unique = true),
        @Index(name = "idx_customer_pagination_id_active", columnList = "id, active")
})
@SQLDelete(sql = "UPDATE customers SET active = false, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("active = true")
public class CustomerEntity extends BaseEntity {

   @OneToOne(fetch = FetchType.LAZY, optional = false,
           cascade = {CascadeType.REFRESH, CascadeType.MERGE})
   @JoinColumn(name = "user_id", nullable = false, unique = true)
   @ToString.Exclude
   @NonNull
   private UserEntity user;

   @Nullable
   @Column(name = "contact", length = 15)
   private String phoneContact;
}
