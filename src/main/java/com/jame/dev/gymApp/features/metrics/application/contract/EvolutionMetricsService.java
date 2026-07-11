package com.jame.dev.gymApp.features.metrics.application.contract;

import com.jame.dev.gymApp.features.metrics.api.response.BillingEvolutionResponse;
import com.jame.dev.gymApp.features.metrics.api.response.CustomerEvolutionResponse;
import com.jame.dev.gymApp.features.metrics.api.response.SubscriberEvolutionResponse;

public interface EvolutionMetricsService {
   CustomerEvolutionResponse getJoiningCustomerEvolution(final long year);

   CustomerEvolutionResponse getDowningCustomerEvolution(final long year);

   SubscriberEvolutionResponse getJoiningSubscriberEvolution(final long year);

   SubscriberEvolutionResponse getDowningSubscribersBeforeEndTime(final long year);

   BillingEvolutionResponse getBillingEvolution(final long year);
}
