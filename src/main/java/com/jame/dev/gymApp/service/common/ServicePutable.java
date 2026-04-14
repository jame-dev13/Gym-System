package com.jame.dev.gymApp.service.common;

import lombok.NonNull;

public interface ServicePutable<@NonNull DTO_OUT, @NonNull D> extends
   BaseService<DTO_OUT, D>,
   Putable<DTO_OUT, D> {
}
