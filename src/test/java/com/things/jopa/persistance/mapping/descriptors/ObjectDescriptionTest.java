package com.things.jopa.persistance.mapping.descriptors;

import com.things.jopa.persistance.annotations.Entity;
import com.things.jopa.testing.TestingCustomEntity;
import com.things.jopa.testing.TestingEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("OptionalGetWithoutIsPresent")
class ObjectDescriptionTest {
    @Test
    public void shouldCreateObjectDescriptionFromObject() {
        //given
        TestingEntity testingEntity = TestingEntity.builder()
                .id(1)
                .string("test")
                .aDouble(2.4)
                .build();
        Class<?> testingClass = TestingEntity.class;

        //when
        ObjectDescription objectDescription = new ObjectDescription(testingEntity);

        //then
        assertThat(objectDescription.getObjectClass()).isEqualTo(testingClass);
        assertThat(objectDescription.getTableName()).isEqualTo(testingClass.getSimpleName());
        assertThat(objectDescription.getFieldDescriptions().size()).isEqualTo(testingClass.getDeclaredFields().length);
        assertThat(objectDescription.getFieldDescriptions().stream()
                .filter(fieldDescription -> fieldDescription.getFieldName().equals("id"))
                .findFirst()
                .get()
                .getValue()).isEqualTo(testingEntity.getId());
        for (ObjectDescription.FieldDescription<?> fieldDescription : objectDescription.getFieldDescriptions()) {
            assertThat(fieldDescription.getColumnName()).isEqualTo(fieldDescription.getFieldName());
        }
    }

    @Test
    public void shouldCreateCustomObjectDescriptionFromObject() {
        //given
        TestingCustomEntity testingEntity = TestingCustomEntity.builder()
                .id(1)
                .string("test")
                .aDouble(2.4)
                .build();

        Class<?> testingClass = TestingCustomEntity.class;

        //when
        ObjectDescription objectDescription = new ObjectDescription(testingEntity);

        //then
        assertThat(objectDescription.getObjectClass()).isEqualTo(testingClass);
        assertThat(objectDescription.getTableName()).isEqualTo(testingClass.getAnnotation(Entity.class).name());
        assertThat(objectDescription.getFieldDescriptions().size()).isEqualTo(testingClass.getDeclaredFields().length);
        assertThat(objectDescription.getFieldDescriptions().stream()
                .filter(fieldDescription -> fieldDescription.getFieldName().equals("id"))
                .findFirst()
                .get()
                .getValue()).isEqualTo(testingEntity.getId());
        for (ObjectDescription.FieldDescription<?> fieldDescription : objectDescription.getFieldDescriptions()) {
            assertThat(fieldDescription.getColumnName()).isEqualTo(fieldDescription.getFieldName());
        }
    }

    //TODO tests for constructors from ResultSet
}