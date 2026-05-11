package com.jame.dev.gymApp.application.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;


/**
 * This Interface is cheater for redis because only set @class in the types
 * that are declared on redisMapper.addMixIn(Complex/GenricType.class, DefaultMixInDto.class)
 * this resolves the Deserialization Exception: InvalidTypeIdException on redis
 * attempts to identify the classes on payload's stored.
 *
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class")
public interface DefaultMixInDto {
}
