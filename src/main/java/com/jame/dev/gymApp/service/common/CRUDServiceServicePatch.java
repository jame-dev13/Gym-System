package com.jame.dev.gymApp.service.common;

import lombok.NonNull;

public interface CRUDServiceServicePatch<@NonNull DTO_OUT, @NonNull D, ID> extends
        BaseCrudService<DTO_OUT, D, ID>,
        Patchable<DTO_OUT, ID> {
}
