package com.jame.dev.gymApp.controller.components;

import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import org.springframework.stereotype.Component;

@Component("authorize")
public class Authorize<DTO_IN> {

   public boolean checkIdentity(DTO_IN dto){
      return (dto instanceof CustomerDtoInput) || (dto instanceof SubscriptionDtoInput);
   }
}
