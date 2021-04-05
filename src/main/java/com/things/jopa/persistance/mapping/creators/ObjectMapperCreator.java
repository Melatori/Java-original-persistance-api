package com.things.jopa.persistance.mapping.creators;

import com.things.jopa.persistance.mapping.ObjectMapper;

public interface ObjectMapperCreator {
    <T> ObjectMapper<T> create(Class<T> objectType);
}
