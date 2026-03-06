package com.jame.dev.gymApp.listeners;

import com.jame.dev.gymApp.model.dto.out.CacheMutated;
import com.jame.dev.gymApp.utils.BeanUtils;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
@Deprecated
public class EntityListener {

   @PostPersist
   @PostUpdate
   @PostRemove
   public void onAnyChange(Object entity) {
      BeanUtils.getContext()
              .publishEvent(new CacheMutated(entity.toString()));
   }
}
