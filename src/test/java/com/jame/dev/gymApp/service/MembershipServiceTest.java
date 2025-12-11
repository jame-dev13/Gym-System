package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.entity.MemberShipEntity;
import com.jame.dev.gymApp.repository.MembershipRepository;
import com.jame.dev.gymApp.service.out.MembershipServiceImplementation;
import com.jame.dev.gymApp.shared.enums.Membership;
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
   private MembershipServiceImplementation service;

   private final MemberShipEntity membershipTest = MemberShipEntity.builder()
           .id(1)
           .membership(Membership.MONTHLY)
           .build();

   @Test
   @DisplayName("should get all memberships.")
   void getAll() {
      when(repo.findAll()).thenReturn(List.of(membershipTest));

      final List<MemberShipEntity> membershipList = service.getAll();
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

      final Optional<MemberShipEntity> optionalMembership = service.getByMembership(membership);
      final MemberShipEntity membershipGotten = optionalMembership.orElseThrow();

      verify(repo, atLeastOnce()).findByMembership(membership);
      verifyNoMoreInteractions(repo);
      assertNotEquals(Optional.empty(), optionalMembership, "optionalMembership should not be Optional.empty()");
      assertNotNull(membershipGotten, "Membership Object should not be null");
      assertEquals(this.membershipTest, membershipGotten, "Membership retrieved should be the same as 'membershipTest'");
   }

   @Test
   void save() {
      when(repo.save(any(MemberShipEntity.class)))
              .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
      final MemberShipEntity membershipAdded = service.save(membershipTest);

      final ArgumentCaptor<MemberShipEntity> captor = ArgumentCaptor.forClass(MemberShipEntity.class);
      verify(repo).save(captor.capture());
      verifyNoMoreInteractions(repo);

      final MemberShipEntity membershipSaved = captor.getValue();

      assertNotNull(membershipSaved, "Should not be null.");
      assertEquals(membershipSaved, membershipAdded, "Should be equals.");
   }
}