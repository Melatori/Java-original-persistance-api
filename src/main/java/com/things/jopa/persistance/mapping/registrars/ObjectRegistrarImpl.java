package com.things.jopa.persistance.mapping.registrars;

import com.things.jopa.persistance.mapping.ObjectMapper;
import com.things.jopa.persistance.mapping.creators.ObjectMapperCreator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ObjectRegistrarImpl implements ObjectRegistrar{
    private final ObjectMapperCreator mapperCreator;

    @Override
    public <T> void register(Class<T> objectType) {
        ObjectMapper<T> objectMapper = mapperCreator.create(objectType);
    }
}
