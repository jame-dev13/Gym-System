package com.jame.dev.gymApp.features.customer.domain.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jame.dev.gymApp.domain.model.BaseEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.jspecify.annotations.Nullable;

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
   @Index(name = "idx_customer_user_id_unq", columnList = "user_id", unique = true),
   @Index(name = "idx_customer_pagination_id_active", columnList = "id, active")
})
@SQLDelete(sql = "UPDATE customers SET active = false, deleted_at = NOW() WHERE id = ?")
public class CustomerEntity extends BaseEntity {

   @OneToOne(fetch = FetchType.LAZY,
      optional = false,
      cascade = {CascadeType.REFRESH, CascadeType.MERGE})
   @JoinColumn(
      name = "user_id",
      nullable = false,
      unique = true,
      foreignKey = @ForeignKey(
         name = "fk_customer_user_id",
         foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
      ))
   @ToString.Exclude
   @NonNull
   private UserEntity user;

   @Nullable
   @Column(name = "contact", length = 15)
   private String phoneContact;

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
