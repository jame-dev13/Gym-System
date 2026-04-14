package com.jame.dev.gymApp.service.common;

public interface FullService<DTO_OUT, DTO_IN> extends
   BaseService<DTO_OUT, DTO_IN>,
   Patchable<DTO_OUT>,
   Putable<DTO_OUT, DTO_IN> {
}
