package com.jame.dev.gymApp.service.common;

public interface FullServiceIdentifiable<OUT, IN, E> extends
   BaseService<OUT, IN>,
   Patchable<OUT>,
   Putable<OUT, IN>,
   EmailIdentifiable<E>{
}
