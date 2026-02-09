package com.jame.dev.gymApp.service.common;

import lombok.NonNull;

public interface CRUDServiceServicePut<@NonNull E, @NonNull D, @NonNull ID> extends
        BaseCrudService<E, D, ID>,
        Putable<E, D, ID> {
}
