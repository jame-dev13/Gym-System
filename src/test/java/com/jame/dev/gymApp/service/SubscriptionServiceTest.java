package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.entity.*;
import com.jame.dev.gymApp.exception.AlreadyExistsException;
import com.jame.dev.gymApp.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.factories.in.Factory;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.model.dto.in.SubscriptionFactoryDtoInput;
import com.jame.dev.gymApp.model.dto.out.PageDto;
import com.jame.dev.gymApp.model.dto.out.SubscriptionDtoOutput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.PricingRepository;
import com.jame.dev.gymApp.repository.SubscriptionRepository;
import com.jame.dev.gymApp.service.out.SubscriptionServiceImplementation;
import com.jame.dev.gymApp.shared.enums.Membership;
import com.jame.dev.gymApp.updaters.SubscriptionUpdater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
   @Mock
   private SubscriptionRepository repo;

   @Mock
   private CustomerRepository customerRepo;

   @Mock
   private PricingRepository pricingRepo;

   @Mock
   private Factory<SubscriptionEntity, SubscriptionDtoOutput, SubscriptionFactoryDtoInput> subscriptionFactory;

   @Mock
   private SubscriptionUpdater subscriptionUpdater;

   @InjectMocks
   private SubscriptionServiceImplementation service;

   private final MemberShipEntity MEMBERSHIP_TEST = new MemberShipEntity(1, Membership.MONTHLY);
   private final PricingEntity pricingMock = new PricingEntity(1, MEMBERSHIP_TEST, BigDecimal.valueOf(300.00));
   private final UserEntity userMock = new UserEntity();
   private final CustomerEntity customerMock = new CustomerEntity();
   private final SubscriptionDtoInput dtoMock = new SubscriptionDtoInput("customer@mail.com", Membership.MONTHLY);
   private final SubscriptionEntity mockSubscription = new SubscriptionEntity();

   private final List<SubscriptionEntity> testSubsList = IntStream.range(0, 10)
           .mapToObj(i -> {
              CustomerEntity customer = new CustomerEntity(
                      new UserEntity(), "32472525" + i);
              PricingEntity pricing = new PricingEntity((i + 1), new MemberShipEntity((i + 1), Membership.MONTHLY), BigDecimal.valueOf(300.00d));
              return SubscriptionEntity.builder()

                      .customer(customer)
                      .pricing(pricing)
                      .subscriptionPeriods(List.of(new PeriodEntity()))
                      .finished(false)
                      .build();
           })
           .toList();

   @BeforeEach
   void setUp() {
      customerMock.setUser(userMock);
      mockSubscription.setCustomer(customerMock);
   }

   @Test
   @DisplayName("Should Get Page of subscriptions")
   void getPageByActive() {
      final Pageable pageable = PageRequest.of(0, 5);
      final List<SubscriptionEntity> subList = testSubsList.subList(0, 5);
      PageDto<SubscriptionDtoOutput> pageDto = mock();
      when(repo.findAll(pageable)).thenReturn(new PageImpl<>(subList));
      when(subscriptionFactory.createPageFrom(any())).thenReturn(pageDto);

      final var page = service.getPage(pageable);
      final var pageContent = page.content();

      assertNotNull(page, "Page shouldn't be null");
      assertNotNull(pageContent, "Page content shouldn't be null");

      verify(repo, atLeastOnce()).findAll(pageable);
      verify(subscriptionFactory, atLeastOnce()).createPageFrom(any());
      verifyNoMoreInteractions(repo);
   }

   @Test
   @DisplayName("Should Get subscription by id")
   void getById() {
      when(repo.findById(anyLong())).thenReturn(Optional.of(this.mockSubscription));

      final var result = service.getById(1L);

      assertTrue(result.isPresent(), "Result should be present.");
      assertNotNull(result.get(), "Result shouldn't be null.");

      verify(repo, atLeastOnce()).findById(anyLong());
      verifyNoMoreInteractions(repo);
   }

   @Test
   @DisplayName("Save subscription")
   void save() {
      SubscriptionDtoOutput output = mock(SubscriptionDtoOutput.class);
      when(customerRepo.findByUser_Email(anyString())).thenReturn(Optional.of(customerMock));
      when(repo.existsByCustomer(customerMock)).thenReturn(false);
      when(pricingRepo.findByMemberShipEntity_Membership(any(Membership.class)))
              .thenReturn(Optional.of(pricingMock));
      when(subscriptionFactory.createFromInput(any(SubscriptionFactoryDtoInput.class)))
              .thenReturn(mockSubscription);
      when(repo.saveAndFlush(mockSubscription))
              .thenReturn(mockSubscription);
      when(subscriptionFactory.createFromEntity(any())).thenReturn(output);

      final var result = service.save(this.dtoMock);

      assertNotNull(result);

      verify(customerRepo, atMostOnce()).findByUser_Email(anyString());
      verify(repo, atMostOnce()).existsByCustomer(customerMock);
      verify(pricingRepo, atMostOnce()).findByMemberShipEntity_Membership(any(Membership.class));
      verify(subscriptionFactory, atMostOnce())
              .createFromInput(any(SubscriptionFactoryDtoInput.class));
      verify(repo, atLeastOnce()).saveAndFlush(mockSubscription);
      verifyNoMoreInteractions(repo, customerRepo, pricingRepo, subscriptionFactory);
   }

   @Test
   @DisplayName("Customer not found")
   void shouldThrowsCustomerNotFound() {
      when(customerRepo.findByUser_Email(anyString())).thenReturn(Optional.empty());

      assertThrows(CustomerNotFoundException.class, () -> service.save(dtoMock));

      verify(customerRepo, atMostOnce()).findByUser_Email(anyString());
      verifyNoInteractions(repo);
      verifyNoMoreInteractions(customerRepo);
   }

   @Test
   @DisplayName("Exists by customer")
   void shouldThrowsAlreadyExistsException() {
      when(customerRepo.findByUser_Email(anyString())).thenReturn(Optional.of(customerMock));
      when(repo.existsByCustomer(customerMock)).thenReturn(true);

      assertThrowsExactly(AlreadyExistsException.class, () -> service.save(dtoMock));

      verify(customerRepo, atMostOnce()).findByUser_Email(anyString());
      verify(repo, atMostOnce()).existsByCustomer(any(CustomerEntity.class));
      verifyNoMoreInteractions(repo, customerRepo);
   }

   @Test
   @DisplayName("Should update the SubscriptionEntity")
   void update() {
      SubscriptionDtoOutput output = mock(SubscriptionDtoOutput.class);
      SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
      subscriptionEntity.setUpdatedAt(Instant.now());

      when(repo.findById(anyLong())).thenReturn(Optional.of(mockSubscription));
      when(pricingRepo.findByMemberShipEntity_Membership(any(Membership.class)))
              .thenReturn(Optional.of(pricingMock));
      when(repo.saveAndFlush(any(SubscriptionEntity.class))).
              thenReturn(subscriptionEntity);
      when(subscriptionFactory.createFromEntity(any())).thenReturn(output);

      final var result = service.update(1L, dtoMock);

      assertNotNull(result);

      verify(repo, atMostOnce()).findById(anyLong());
      verify(pricingRepo, atMostOnce()).findByMemberShipEntity_Membership(any(Membership.class));
      verify(repo, atMostOnce()).saveAndFlush(subscriptionEntity);
      verify(subscriptionFactory, atMostOnce()).createFromEntity(any());
      verifyNoMoreInteractions(repo, pricingRepo);
   }

   @Test
   @DisplayName("Should finalize the subscription [patch]")
   void finalizeSubscription() {
      SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
      subscriptionEntity.setFinished(true);
      subscriptionEntity.setUpdatedAt(Instant.now());

      when(repo.findById(anyLong())).thenReturn(Optional.of(mockSubscription));
      when(repo.save(any(SubscriptionEntity.class))).thenReturn(subscriptionEntity);

      final var result = service.patch(1L);

      assertNotNull(result);

      verify(repo, atMostOnce()).findById(anyLong());
      verify(repo, atMostOnce()).save(subscriptionEntity);
   }

   @Test
   @DisplayName("Should renew the subscription")
   void shouldRenewSubscription() {
      String email = "sample@mail.com";
      LocalDate now = LocalDate.now();
      SubscriptionDtoInput dtoMock = new SubscriptionDtoInput(email, Membership.MONTHLY);

      UserEntity userMocked = mock(UserEntity.class);
      CustomerEntity customerMocked = mock(CustomerEntity.class);
      PeriodEntity lastPeriod = mock(PeriodEntity.class);
      SubscriptionEntity subscription = mock(SubscriptionEntity.class);
      PricingEntity pricingMock = mock(PricingEntity.class);

      SubscriptionDtoOutput output = mock(SubscriptionDtoOutput.class);

      when(subscription.isFinished()).thenReturn(true);
      when(subscription.getCustomer()).thenReturn(customerMocked);
      when(customerMocked.getUser()).thenReturn(userMocked);
      when(userMocked.getEmail()).thenReturn(email);

      LinkedList<PeriodEntity> periods = new LinkedList<>(List.of(lastPeriod));
      when(subscription.getSubscriptionPeriods()).thenReturn(periods);
      when(lastPeriod.getEndPeriod()).thenReturn(now);

      when(repo.findById(1L)).thenReturn(Optional.of(subscription));
      when(pricingRepo.findByMemberShipEntity_Membership(any())).thenReturn(Optional.of(pricingMock));

      when(repo.saveAndFlush(any())).thenReturn(subscription);
      when(subscriptionFactory.createFromEntity(any())).thenReturn(output);

      final var result = service.put(1L, dtoMock);

      assertNotNull(result);
      verify(repo).findById(1L);
      verify(repo).saveAndFlush(subscription);
      verify(subscriptionFactory, atMostOnce()).createFromEntity(any());
   }

   @Test
   @DisplayName("Should delete subscription by Id.")
   void softDeleteById() {
      service.softDelete(1L);
      verify(repo, times(1)).deleteById(anyLong());
   }
}