package com.jame.dev.gymApp.repository.common;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@NoRepositoryBean //Avoids that spring tries to create an instance of this class.
public interface CustomJpaRepository<T, ID> extends JpaRepository<@NonNull T, @NonNull ID> {

   @Modifying
   @Transactional
   @Query("UPDATE #{#entityName} e SET e.active = false WHERE e.id = :id")
   void softDelete(@Param("id") ID id);

   List<T> findByActiveTrue();
}