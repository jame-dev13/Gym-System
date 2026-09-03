package com.jame.dev.gymApp.features.customer.domain.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "customers", indexes = {
   @Index(name = "idx_customer_pagination_id_active", columnList = "id, active")
})
@SQLDelete(sql = "UPDATE customers SET active = false, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("active = true")
@EntityListeners(AuditingEntityListener.class)
public class CustomerEntity {

   @Id
   @Setter(AccessLevel.NONE)
   private Long id;

   @MapsId
   @OneToOne(fetch = FetchType.LAZY,
      optional = false,
      cascade = {CascadeType.REFRESH, CascadeType.MERGE})
   @JoinColumn(
      name = "id",
      nullable = false,
      foreignKey = @ForeignKey(
         name = "fk_customer_user_id",
         foreignKeyDefinition = "FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE"
      ))
   @ToString.Exclude
   @NonNull
   private UserEntity user;

   @Nullable
   @Column(name = "contact", length = 15)
   private String phoneContact;

   @Column(name = "created_at", updatable = false, nullable = false)
   @CreatedDate
   protected Instant createdAt;

   @Column(name = "updated_at")
   @LastModifiedDate
   protected Instant updatedAt;

   @Column(name = "deleted_at")
   protected Instant deletedAt;

   @Column(name = "active", nullable = false)
   @Builder.Default
   protected boolean active = true;

   @OneToMany(
      mappedBy = "customer",
      orphanRemoval = true,
      fetch = FetchType.LAZY,
      cascade = {CascadeType.MERGE, CascadeType.REMOVE}
   )
   @Builder.Default
   @JsonIgnore
   private List<SubscriptionEntity> subscriptions = new LinkedList<>();
}
