package com.things.jopa.persistance.mapping.descriptors;

import com.things.jopa.persistance.annotations.Column;
import com.things.jopa.persistance.annotations.Entity;
import com.things.jopa.persistance.annotations.PrimaryKey;
import com.things.jopa.persistance.exceptions.JopaException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.h2.util.StringUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Data
public class ObjectDescription {
    private String tableName;
    private Class<?> objectClass;
    private List<FieldDescription<?>> fieldDescriptions;

    public ObjectDescription(Object object) {
        Class<?> clazz = object.getClass();
        tableName = (clazz.getAnnotation(Entity.class) != null) && (!StringUtils.isNullOrEmpty(clazz.getAnnotation(Entity.class).name())) ?
                clazz.getAnnotation(Entity.class).name() :
                clazz.getSimpleName();

        objectClass = clazz;

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

            field.setAccessible(true);

            try {
                fieldDescriptions.add(new FieldDescription<>(
                        columnName,
                        fieldName,
                        field.get(object),
                        isKey
                ));
            } catch (IllegalAccessException e) {
                throw new JopaException("Things went wrong with creation ObjectDescriptor from Object", e);
            }
        }
    }

    public <T> ObjectDescription(ResultSet resultSet, Class<T> clazz) {
        tableName = (clazz.getAnnotation(Entity.class) != null) && (!StringUtils.isNullOrEmpty(clazz.getAnnotation(Entity.class).name())) ?
                clazz.getAnnotation(Entity.class).name() :
                clazz.getSimpleName();

        objectClass = clazz;

        fieldDescriptions = new ArrayList<>();

        Field[] declaredFields = objectClass.getDeclaredFields();
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

            field.setAccessible(true);

            try {
                ResultSetMetaData metaData = resultSet.getMetaData();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    Class<?> fieldClass = field.getDeclaringClass();

                    if (metaData.getColumnName(i).equals(columnName)) {
                        fieldDescriptions.add(new FieldDescription<>(
                                columnName,
                                fieldName,
                                fieldClass.cast(resultSet.getObject(i)),
                                isKey
                        ));
                        break;
                    }
                }
            } catch (SQLException e) {
                throw new JopaException("Things went wrong with creation ObjectDescriptor from ResultSet", e);
            }
        }
    }

    @Data
    @AllArgsConstructor
    public static class FieldDescription<T> {
        private String columnName;
        private String fieldName;
        private T value;
        private Boolean isKey;
    }
}
