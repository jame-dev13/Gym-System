package com.jame.dev.gymApp.domain.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface CustomJpaRepository<T, ID> extends 
   JpaRepository<@NonNull T, @NonNull ID>,
   JpaSpecificationExecutor<T> {
}