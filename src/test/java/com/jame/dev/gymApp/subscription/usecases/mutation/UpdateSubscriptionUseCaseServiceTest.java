package com.jame.dev.gymApp.subscription.usecases.mutation;

import com.jame.dev.gymApp.domain.exception.UnrelatedDataAccessException;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionUpdater;
import com.jame.dev.gymApp.features.subscription.application.service.mutation.UpdateSubscriptionUseCaseService;
import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import com.jame.dev.gymApp.features.subscription.domain.model.MembershipEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionValidationRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.MembershipRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateSubscriptionUseCaseServiceTest {

   @Mock
   private SubscriptionQueryRepository subscriptionQueryRepository;

   @Mock
   private SubscriptionValidationRepository subscriptionValidationRepository;

   @Mock
   private MembershipRepository membershipRepository;

   @Mock
   private SubscriptionUpdater subscriptionUpdater;

   @Mock
   private SubscriptionMutationRepository subscriptionMutationRepository;

   @Mock
   private SubscriptionFactory subscriptionFactory;

   @InjectMocks
   private UpdateSubscriptionUseCaseService service;

   private final SubscriptionRequest request = new SubscriptionRequest("customer@mail.com", Membership.MONTHLY);

   @Test
   @DisplayName("Should update and return SubscriptionResponse when subscription exists")
   void update_updatesAndReturnsResponse() {
      var subscriptionEntity = new SubscriptionEntity();
      subscriptionEntity.setUpdatedAt(Instant.now());
      var membership = new MembershipEntity();
      var modifiedEntity = new SubscriptionEntity();
      var response = mock(SubscriptionResponse.class);

      given(subscriptionValidationRepository.existsByIdAndCustomerEmail(anyLong(), anyString()))
         .willReturn(true);
      given(subscriptionQueryRepository.findById(anyLong())).willReturn(Optional.of(subscriptionEntity));
      given(membershipRepository.findByMembership(any(Membership.class))).willReturn(Optional.of(membership));
      willDoNothing().given(subscriptionUpdater).apply(any(SubscriptionEntity.class), any(MembershipEntity.class));
      given(subscriptionMutationRepository.save(any(SubscriptionEntity.class))).willReturn(modifiedEntity);
      given(subscriptionFactory.createFromEntity(any(SubscriptionEntity.class))).willReturn(response);

      var result = assertDoesNotThrow(() -> service.update(1L, request));

      assertNotNull(result);
      verify(subscriptionValidationRepository).existsByIdAndCustomerEmail(anyLong(), anyString());
      verify(subscriptionQueryRepository).findById(anyLong());
      verify(membershipRepository).findByMembership(any(Membership.class));
      verify(subscriptionUpdater).apply(any(SubscriptionEntity.class), any(MembershipEntity.class));
      verify(subscriptionMutationRepository).save(any(SubscriptionEntity.class));
      verify(subscriptionFactory).createFromEntity(any(SubscriptionEntity.class));
      verifyNoMoreInteractions(subscriptionQueryRepository, subscriptionValidationRepository, membershipRepository, subscriptionUpdater, subscriptionMutationRepository, subscriptionFactory);
   }

   @Test
   @DisplayName("Should throw UnrelatedDataAccessException when not exists by sub id's and customer email's.")
   void update_whenSubscriptionIdAndEmail_Unrelated_throwsException() {
      given(subscriptionValidationRepository.existsByIdAndCustomerEmail(anyLong(), anyString()))
         .willReturn(false);

      assertThrowsExactly(UnrelatedDataAccessException.class, () -> service.update(1L, request));
      verify(subscriptionValidationRepository, atLeastOnce()).existsByIdAndCustomerEmail(anyLong(), anyString());
      verifyNoMoreInteractions(subscriptionValidationRepository);
      verifyNoInteractions(subscriptionQueryRepository, subscriptionMutationRepository, subscriptionUpdater, subscriptionFactory);
   }

   @Test
   @DisplayName("Should throw SubscriptionNotFoundException when subscription not found")
   void update_whenSubscriptionNotFound_throwsException() {
      given(subscriptionValidationRepository.existsByIdAndCustomerEmail(anyLong(), anyString()))
         .willReturn(true);
      given(subscriptionQueryRepository.findById(anyLong())).willReturn(Optional.empty());

      assertThrows(SubscriptionNotFoundException.class, () -> service.update(1L, request));

      verify(subscriptionValidationRepository).existsByIdAndCustomerEmail(anyLong(), anyString());
      verify(subscriptionQueryRepository).findById(anyLong());
      verifyNoInteractions(membershipRepository, subscriptionUpdater, subscriptionMutationRepository, subscriptionFactory);
   }

   @Test
   @DisplayName("Should throw NotFoundException when membership not found")
   void update_whenMembershipNotFound_throwsException() {
      var subscriptionEntity = new SubscriptionEntity();
      given(subscriptionValidationRepository.existsByIdAndCustomerEmail(anyLong(), anyString()))
         .willReturn(true);
      given(subscriptionQueryRepository.findById(anyLong())).willReturn(Optional.of(subscriptionEntity));
      given(membershipRepository.findByMembership(any(Membership.class))).willReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> service.update(1L, request));

      verify(subscriptionValidationRepository).existsByIdAndCustomerEmail(anyLong(), anyString());
      verify(subscriptionQueryRepository).findById(anyLong());
      verify(membershipRepository).findByMembership(any(Membership.class));
      verifyNoInteractions(subscriptionUpdater, subscriptionMutationRepository, subscriptionFactory);
   }
}
