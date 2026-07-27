package com.jame.dev.gymApp.features.metrics.application.service;

import com.jame.dev.gymApp.features.metrics.api.response.BillingEvolutionResponse;
import com.jame.dev.gymApp.features.metrics.api.response.CustomerEvolutionResponse;
import com.jame.dev.gymApp.features.metrics.api.response.SubscriberEvolutionResponse;
import com.jame.dev.gymApp.features.metrics.application.contract.EvolutionMetricsService;
import com.jame.dev.gymApp.features.metrics.domain.repository.EvolutionMetricsRepository;
import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheEvolutionMetricsValues;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
public class EvolutionMetricsApplicationService implements EvolutionMetricsService {
   private final EvolutionMetricsRepository evolutionMetricsRepository;

   @Override
   @Cacheable(
      value = CacheEvolutionMetricsValues.JOINING_CUSTOMERS,
      key = "#year",
      unless = "#result == null"
   )
   public CustomerEvolutionResponse getJoiningCustomerEvolution(long year) {
      return new CustomerEvolutionResponse(evolutionMetricsRepository.calculateJoiningCustomerEvolution(year));
   }

   @Override
   @Cacheable(
      value = CacheEvolutionMetricsValues.DOWNING_CUSTOMERS,
      key = "#year",
      unless = "#result == null"
   )
   public CustomerEvolutionResponse getDowningCustomerEvolution(long year) {
      return new CustomerEvolutionResponse(evolutionMetricsRepository.calculateDowningCustomerEvolution(year));
   }

   @Override
   @Cacheable(
      value = CacheEvolutionMetricsValues.JOINING_SUBSCRIBERS,
      key = "#year",
      unless = "#result == null"
   )
   public SubscriberEvolutionResponse getJoiningSubscriberEvolution(long year) {
      return new SubscriberEvolutionResponse(evolutionMetricsRepository.calculateJoiningSubscriberEvolution(year));
   }

   @Override
   @Cacheable(
      value = CacheEvolutionMetricsValues.DOWNING_SUBSCRIBERS,
      key = "#year",
      unless = "#result == null"
   )
   public SubscriberEvolutionResponse getDowningSubscribersBeforeEndTime(long year) {
      return new SubscriberEvolutionResponse(evolutionMetricsRepository.calculateDowningSubscribersBeforeEndTime(year));
   }

   @Override
   @Cacheable(
      value = CacheEvolutionMetricsValues.BILLINGS,
      key = "#year",
      unless = "#result == null"
   )
   public BillingEvolutionResponse getBillingEvolution(long year) {
      return new BillingEvolutionResponse(evolutionMetricsRepository.calculateBillingEvolution(year));
   }
}
