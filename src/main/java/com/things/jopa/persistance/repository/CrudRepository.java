package com.things.jopa.persistance.repository;

import java.util.Collection;

public interface CrudRepository<T, K>{
    Collection<T> getAll();
    T getById(K key);
    T post(T object);
    T put(T object);
    void delete(K key);
}
