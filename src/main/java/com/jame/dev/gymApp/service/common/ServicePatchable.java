package com.jame.dev.gymApp.service.common;

import lombok.NonNull;

public interface ServicePatchable<@NonNull DTO_OUT, @NonNull D> extends
   BaseService<DTO_OUT, D>,
   Patchable<DTO_OUT> {
}
