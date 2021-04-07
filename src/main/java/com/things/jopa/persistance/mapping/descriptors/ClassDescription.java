package com.things.jopa.persistance.mapping.descriptors;

import com.things.jopa.persistance.annotations.Column;
import com.things.jopa.persistance.annotations.Entity;
import com.things.jopa.persistance.annotations.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.h2.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ClassDescription {
    private final String tableName;
    private final Class<?> clazz;
    private final List<FieldDescription> fieldDescriptions;

    public ClassDescription(Class<?> clazz) {
        tableName = (clazz.getAnnotation(Entity.class) != null) && (!StringUtils.isNullOrEmpty(clazz.getAnnotation(Entity.class).name())) ?
                clazz.getAnnotation(Entity.class).name() :
                clazz.getSimpleName();

        this.clazz = clazz;

        fieldDescriptions = new ArrayList<>();

        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            String columnName;
            String fieldName;
            boolean isKey;

            Column column = field.getAnnotation(Column.class);
            if (column == null)
                columnName = field.getName();
            else
                columnName = StringUtils.isNullOrEmpty(column.name()) ? field.getName() : column.name();

            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            isKey = primaryKey != null;

            fieldName = field.getName();

            fieldDescriptions.add(new FieldDescription(
                    columnName,
                    fieldName,
                    field.getType(),
                    isKey
            ));
        }
    }

    @Data
    @AllArgsConstructor
    public static class FieldDescription {
        private String columnName;
        private String fieldName;
        private Class<?> fieldClass;
        private Boolean isKey;
    }
}
