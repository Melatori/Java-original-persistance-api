package com.things.jopa.persistance.mapping.descriptors;

import com.things.jopa.persistance.annotations.Entity;
import com.things.jopa.testing.TestingCustomEntity;
import com.things.jopa.testing.TestingEntity;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ClassDescriptionTest {
    @Test
    public void shouldCreateClassDescription() {
        //given
        Class<?> testingClass = TestingEntity.class;

        //when
        ClassDescription classDescription = new ClassDescription(testingClass);

        //then
        assertThat(classDescription.getClazz()).isEqualTo(testingClass);
        assertThat(classDescription.getTableName()).isEqualTo(testingClass.getSimpleName());
        assertThat(classDescription.getFieldDescriptions().size()).isEqualTo(testingClass.getDeclaredFields().length);
        for (ClassDescription.FieldDescription fieldDescription : classDescription.getFieldDescriptions()) {
            assertThat(Arrays.stream(testingClass.getDeclaredFields())
                    .anyMatch(field -> fieldDescription.getFieldClass() == field.getDeclaringClass())).isTrue();
            assertThat(fieldDescription.getColumnName()).isEqualTo(fieldDescription.getFieldName());
        }
    }

    @Test
    public void shouldCreateCustomClassDescription() {
        //given
        Class<?> testingClass = TestingCustomEntity.class;

        //when
        ClassDescription classDescription = new ClassDescription(testingClass);

        //then
        assertThat(classDescription.getClazz()).isEqualTo(testingClass);
        assertThat(classDescription.getTableName()).isEqualTo(testingClass.getAnnotation(Entity.class).name());
        assertThat(classDescription.getFieldDescriptions().size()).isEqualTo(testingClass.getDeclaredFields().length);
        for (ClassDescription.FieldDescription fieldDescription : classDescription.getFieldDescriptions()) {
            assertThat(Arrays.stream(testingClass.getDeclaredFields())
                    .anyMatch(field -> fieldDescription.getFieldClass() == field.getDeclaringClass())).isTrue();
        }
    }
}