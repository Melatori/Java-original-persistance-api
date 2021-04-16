package com.things.jopa.persistance.mapping;

import com.things.jopa.persistance.mapping.descriptors.ObjectDescription;

public interface ObjectMapper <T> {
    T convertToObject(ObjectDescription descriptor);
}
