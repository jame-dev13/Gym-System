package com.jame.dev.gymApp.service.common;

import lombok.NonNull;

public interface CRUDServiceServicePut<@NonNull DTO_OUT, @NonNull D, @NonNull ID> extends
        BaseCrudService<DTO_OUT, D, ID>,
        Putable<DTO_OUT, D, ID> {
}
