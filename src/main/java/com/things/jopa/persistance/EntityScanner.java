package com.things.jopa.persistance;

import com.things.jopa.persistance.annotations.Entity;
import com.things.jopa.persistance.mapping.descriptors.ClassDescription;
import org.reflections.Reflections;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityScanner {
    public List<ClassDescription> scan() {
        Reflections reflections = new Reflections("com.things.jopa");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Entity.class);

        return annotated.stream()
                .map(ClassDescription::new)
                .collect(Collectors.toList());
    }
}
