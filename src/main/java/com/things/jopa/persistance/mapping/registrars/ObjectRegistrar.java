package com.things.jopa.persistance.mapping.registrars;

public interface ObjectRegistrar {
    <T> void register(Class<T> objectType);
}
