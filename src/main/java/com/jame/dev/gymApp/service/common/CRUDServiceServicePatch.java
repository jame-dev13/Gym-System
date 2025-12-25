package com.jame.dev.gymApp.service.common;

import lombok.NonNull;

public interface CRUDServiceServicePatch<@NonNull E, @NonNull D, ID> extends
        BaseCrudService<E, D, ID>,
        Patchable<E, ID> {
}
