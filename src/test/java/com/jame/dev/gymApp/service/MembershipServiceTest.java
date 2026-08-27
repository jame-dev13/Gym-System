package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.features.subscription.domain.model.MembershipEntity;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.MembershipRepository;
import com.jame.dev.gymApp.features.subscription.application.service.MembershipApplicationService;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {

   @Mock
   private MembershipRepository repo;

   @InjectMocks
   private MembershipApplicationService service;

   private final MembershipEntity membershipTest = MembershipEntity.builder()
           .id(1)
           .membership(Membership.MONTHLY)
           .build();

   @Test
   @DisplayName("should get all memberships.")
   void getAll() {
      when(repo.findAll()).thenReturn(List.of(membershipTest));

      final List<MembershipEntity> membershipList = service.getAll();
      verify(repo, atLeastOnce()).findAll();
      verifyNoMoreInteractions(repo);

      assertNotNull(membershipList, "List should not be null.");
      assertFalse(membershipList.isEmpty(), "List should not be empty.");
      assertEquals(1, membershipList.size(), "List should have size of 1.");
      assertTrue(membershipList.contains(this.membershipTest), "List should contain 'membershipTest'.");
   }

   @Test
   @DisplayName("Should Get by membership.")
   void getByMembership() {
      final Membership membership = this.membershipTest.getMembership();
      when(repo.findByMembership(membership)).thenReturn(Optional.of(membershipTest));

      final Optional<MembershipEntity> optionalMembership = service.getByMembership(membership);
      final MembershipEntity membershipGotten = optionalMembership.orElseThrow();

      verify(repo, atLeastOnce()).findByMembership(membership);
      verifyNoMoreInteractions(repo);
      assertNotEquals(Optional.empty(), optionalMembership, "optionalMembership should not be Optional.empty()");
      assertNotNull(membershipGotten, "Membership Object should not be null");
      assertEquals(this.membershipTest, membershipGotten, "Membership retrieved should be the same as 'membershipTest'");
   }

   @Test
   void save() {
      when(repo.save(any(MembershipEntity.class)))
              .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
      final MembershipEntity membershipAdded = service.save(membershipTest);

      final ArgumentCaptor<MembershipEntity> captor = ArgumentCaptor.forClass(MembershipEntity.class);
      verify(repo).save(captor.capture());
      verifyNoMoreInteractions(repo);

      final MembershipEntity membershipSaved = captor.getValue();

      assertNotNull(membershipSaved, "Should not be null.");
      assertEquals(membershipSaved, membershipAdded, "Should be equals.");
   }
}