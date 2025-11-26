package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.entity.MemberShipEntity;
import com.jame.dev.gymApp.exception.NoOperationException;
import com.jame.dev.gymApp.repository.MembershipRepository;
import com.jame.dev.gymApp.service.out.MembershipServiceImplementation;
import com.jame.dev.gymApp.shared.enums.Membership;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {

   @Mock
   private MembershipRepository repo;

   @InjectMocks
   private MembershipServiceImplementation service;

   private MemberShipEntity membershipTest;

   @BeforeEach
   void setUp() {
      this.membershipTest = MemberShipEntity.builder()
              .id(1)
              .membership(Membership.MONTHLY)
              .build();
   }

   @Test
   @DisplayName("Get all")
   void getAll() {
      when(repo.findAll()).thenReturn(List.of(membershipTest));

      var membershipList = service.getAll();
      verify(repo).findAll();

      Assertions.assertNotNull(membershipList, "List should not be null.");
      Assertions.assertFalse(membershipList.isEmpty(), "List should not be empty.");
      Assertions.assertEquals(1, membershipList.size(), "List should have size of 1.");
      Assertions.assertTrue(membershipList.contains(this.membershipTest), "List should contain 'membershipTest'.");
   }

   @Test
   @DisplayName("Get by membership.")
   void getByMembership() {
      Membership membership = this.membershipTest.getMembership();
      when(repo.findByMembership(membership)).thenReturn(Optional.of(membershipTest));

      var optionalMembership = service.getByMembership(membership);
      var membershipGotten = optionalMembership.orElseThrow();
      verify(repo).findByMembership(membership);
      Assertions.assertNotEquals(Optional.empty(), optionalMembership, "optionalMembership should not be Optional.empty()");
      Assertions.assertNotNull(membershipGotten, "Membership Object should not be null");
      Assertions.assertEquals(this.membershipTest, membershipGotten, "Membership retrieved should be the same as 'membershipTest'");
   }

   @Test
   void save() {
      when(repo.save(any(MemberShipEntity.class)))
              .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
      MemberShipEntity membershipAdded = service.save(membershipTest);

      ArgumentCaptor<MemberShipEntity> captor = ArgumentCaptor.forClass(MemberShipEntity.class);
      verify(repo).save(captor.capture());

      MemberShipEntity membershipSaved = captor.getValue();
      Assertions.assertNotNull(membershipSaved, "Should not be null.");
      Assertions.assertEquals(membershipSaved, membershipAdded, "Should be equals.");
   }

   @Test
   void findById() {
      final Integer ID = membershipTest.getId();
      when(repo.findById(ID)).thenReturn(Optional.of(membershipTest));

      var optionalMembership = service.findById(ID);
      verify(repo).findById(ID);
      Assertions.assertNotEquals(Optional.empty(), optionalMembership, "Should not be Optional.empty.");
      Assertions.assertDoesNotThrow(optionalMembership::get, "Should does not throw anything.");
   }

   @Test
   void deleteById() {
      final Integer ID = this.membershipTest.getId();
      Assertions.assertThrows(NoOperationException.class, () -> {
         service.deleteById(ID);
      }, "Should throws an Exception.");
   }
}