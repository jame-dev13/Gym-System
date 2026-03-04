package com.jame.dev.gymApp.service.common;

public interface ServiceComplex<DTO_OUT, DTO_IN, E> extends
        BaseCrudService<DTO_OUT, DTO_IN>,
        Patchable<DTO_OUT>,
        Putable<DTO_OUT, DTO_IN>,
        EmailIdentifiable<E> {
}
