package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.entity.SubscriptionDateEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionDateRepository extends JpaRepository<@NonNull SubscriptionDateEntity, @NonNull Long> {
}
