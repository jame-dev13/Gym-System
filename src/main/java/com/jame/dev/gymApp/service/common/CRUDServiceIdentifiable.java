package com.jame.dev.gymApp.service.common;

public interface CRUDServiceIdentifiable<E, D, ID>
        extends EmailIdentifiable<E>,
        CRUDServiceServicePatch<E, D, ID>,
        CRUDServiceServicePut<E, D, ID> {
}
