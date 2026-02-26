package com.jame.dev.gymApp.service.common;

import lombok.NonNull;

public interface CRUDServiceServicePut<@NonNull DTO_OUT, @NonNull D> extends
        BaseCrudService<DTO_OUT, D>,
        Putable<DTO_OUT, D> {
}
