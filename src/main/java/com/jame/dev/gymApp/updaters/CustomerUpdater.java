package com.jame.dev.gymApp.updaters;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CustomerUpdater {

   public void apply(final CustomerEntity customer, final CustomerDtoInput dto) {
      final UserEntity user = customer.getUser();
      user.setEmail(dto.email());
      customer.setPhoneContact(dto.contact());
      customer.setUpdatedAt(Instant.now());
   }

}
