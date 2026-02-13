package com.jame.dev.gymApp.repository.common;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface CustomJpaRepository<T, ID> extends JpaRepository<@NonNull T, @NonNull ID> {

   Page<@NonNull T> findAllByActiveTrue(@NonNull final Pageable pageable);
}