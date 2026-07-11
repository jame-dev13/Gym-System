package com.jame.dev.gymApp.features.metrics.domain.repository;

import com.jame.dev.gymApp.features.metrics.domain.model.CustomerEvolution;
import com.jame.dev.gymApp.features.metrics.domain.model.SubscriberEvolution;
import com.jame.dev.gymApp.features.metrics.domain.model.MonthTotal;

import java.util.List;

public interface EvolutionMetricsRepository {

   List<CustomerEvolution> calculateJoiningCustomerEvolution(final long year);

   List<CustomerEvolution> calculateDowningCustomerEvolution(final long year);

   List<SubscriberEvolution> calculateJoiningSubscriberEvolution(final long year);

   List<SubscriberEvolution> calculateDowningSubscribersBeforeEndTime(final long year);

   List<MonthTotal> calculateBillingEvolution(final long year);
}
