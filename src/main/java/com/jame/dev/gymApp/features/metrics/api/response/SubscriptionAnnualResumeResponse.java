package com.jame.dev.gymApp.features.metrics.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubscriptionAnnualResumeResponse(
   @JsonProperty("subscriptionCount") long subscriptionCount,
   @JsonProperty("biweeklyTotal") long biweeklyTotal,
   @JsonProperty("monthlyTotal") long monthlyTotal,
   @JsonProperty("quarterlyTotal") long quarterlyTotal,
   @JsonProperty("annualTotal") long annualTotal,
   @JsonProperty("mostRequestedMembership") String mostRequestedMembership
   ) {
}
