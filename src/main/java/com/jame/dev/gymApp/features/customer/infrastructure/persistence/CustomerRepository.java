package com.jame.dev.gymApp.features.customer.infrastructure.persistence;

import com.jame.dev.gymApp.domain.repository.CustomJpaRepository;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends CustomJpaRepository<CustomerEntity, Long> {
   Optional<CustomerEntity> findByUser_Email(final String email);

   @Query(nativeQuery = true, value = """
      SELECT c.* FROM customers c WHERE c.user_id = :userId
      """)
   Optional<CustomerEntity> findDeactivatedByUserId(@Param("userId") final long userId);

   @NativeQuery("""
      SELECT c.* FROM customers c
          LEFT JOIN users u
          ON u.id = c.user_id
          WHERE u.email = :email AND c.active = false
      """)
   Optional<CustomerEntity> findDeactivatedByUserEmail(@Param("email") final String email);

   boolean existsByIdAndUser_Email(long id, String email);

   @Query(nativeQuery = true, value = """
      SELECT EXISTS(
               SELECT 1 FROM customers c
               WHERE c.user_id = :userId)
      """)
   boolean existsDeactivatedByUserId(@Param("userId") final long userId);

   @Modifying(clearAutomatically = true, flushAutomatically = true)
   @NativeQuery(value = """
      UPDATE customers c SET active = true WHERE user_id = :userId
      """)
   void activateByUserId(@Param("userId") long userId);

   @Modifying(clearAutomatically = true, flushAutomatically = true)
   @NativeQuery("""
      UPDATE customers c SET active = true
      FROM users u
      WHERE u.id = c.user_id AND u.email = :email
      """)
   void recoverCustomer(@Param("email") final String email);

   boolean existsByUser(final UserEntity userEntity);

   @NativeQuery(
      """
         SELECT EXISTS(
                 SELECT 1 FROM customers c
                 WHERE c.user_id = :id AND c.active = false
               )
         """)
   boolean existsByUserIdAndActiveFalse(@Param("id") final long userId);

   @NativeQuery("""
      SELECT c.* FROM customers c
      WHERE c.id = :id AND
      c.active = false
      """)
   Optional<CustomerEntity> findDeactivatedById(@Param("id") long id);

   @Modifying(clearAutomatically = true, flushAutomatically = true)
   @NativeQuery(value = """
      DELETE FROM customers c
      WHERE c.id = :id AND
      c.active = false
      """)
   void hardDeleteById(@Param("id") long id);

   void deleteByUser_Id(long userId);

   @Query("""
          SELECT c.id
          FROM CustomerEntity c
          WHERE c.user.email = :email
      """)
   Optional<Long> findIdByUserEmail(@Param("email") String email);

   boolean existsByUser_Email(final String userEmail);
}
