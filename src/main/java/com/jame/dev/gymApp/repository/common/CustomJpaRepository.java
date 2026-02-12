package com.jame.dev.gymApp.repository.common;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


@NoRepositoryBean
public interface CustomJpaRepository<T, ID> extends JpaRepository<@NonNull T, @NonNull ID> {

   @Modifying(clearAutomatically = true)
   @Transactional
   @Query(value = "UPDATE #{#entityName} e SET e.active = false WHERE e.id = :id")
   void softDelete(@Param("id") ID id);

   Page<@NonNull T> findAllByActiveTrue(@NonNull final Pageable pageable);
}