package com.things.jopa.persistance.mapping;

import com.things.jopa.persistance.exceptions.JopaException;
import com.things.jopa.persistance.mapping.descriptors.ObjectDescription;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ObjectMapperImpl<T> implements ObjectMapper<T> {
    @Override
    public T convertToObject(ObjectDescription descriptor) {
        Class<?> objectClass = descriptor.getObjectClass();
        T object = null;
        try {
            object = (T) objectClass.getConstructor().newInstance();

            for (ObjectDescription.FieldDescription<?> fieldDescription : descriptor.getFieldDescriptions()) {
                Field field = objectClass.getField(fieldDescription.getFieldName());
                field.setAccessible(true);
                field.set(object, fieldDescription.getValue());
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new JopaException("Failed to create object of class: [" + objectClass.getName() + "] NoArgs constructor required");
        } catch (NoSuchFieldException e) {
            throw new JopaException("Failed to fill object's field due to unexpected inconsistency at class: [" + objectClass.getName() + "]");
        }
        return object;
    }

    @Override
    public ObjectDescription convertToDescriptor(T object) {
        return new ObjectDescription(object);
    }
}
