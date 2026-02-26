package com.jame.dev.gymApp.service.common;

import lombok.NonNull;

public interface CRUDServiceServicePatch<@NonNull DTO_OUT, @NonNull D> extends
        BaseCrudService<DTO_OUT, D>,
        Patchable<DTO_OUT> {
}
