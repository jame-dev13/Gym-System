package com.jame.dev.gymApp.updaters.imp;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.updaters.in.CustomerUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CustomerUpdaterImplementation implements CustomerUpdater {

   @Override
   public void apply(final CustomerEntity customer, final CustomerDtoInput dto) {
      final UserEntity user = customer.getUser();
      user.setEmail(dto.email());
      customer.setPhoneContact(dto.contact());
      customer.setUpdatedAt(Instant.now());
   }
}
