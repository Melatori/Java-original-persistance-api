package com.things.jopa.persistance.utils;

import com.things.jopa.persistance.mapping.descriptors.ClassDescription;
import com.things.jopa.persistance.mapping.descriptors.ObjectDescription;
import com.things.jopa.persistance.validation.ClassDescriptionValidator;
import com.things.jopa.persistance.validation.ObjectDescriptionValidator;

public class QueryCreator {
    private static final ClassDescriptionValidator classDescriptionValidator = new ClassDescriptionValidator();
    private static final ObjectDescriptionValidator objectDescriptionValidator = new ObjectDescriptionValidator();

    public static String createSelectAllQuery(ClassDescription classDescription) {
        classDescriptionValidator.validate(classDescription);

        String tableName = classDescription.getTableName();
        String[] columnNames = classDescription.getFieldDescriptions().stream()
                .map(ClassDescription.FieldDescription::getColumnName)
                .toArray(String[]::new);

        StringBuilder queryBuilder = new StringBuilder("select ");
        for (int i = 0; i < columnNames.length; i++) {
            if (i != 0)
                queryBuilder.append(", ");

            queryBuilder.append(columnNames[i]);
        }
        queryBuilder.append(" from ")
                .append(tableName)
                .append(";");

        return queryBuilder.toString();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static String createSelectByIdQuery(ClassDescription classDescription, Object id) {
        classDescriptionValidator.validate(classDescription);

        String tableName = classDescription.getTableName();
        String[] columnNames = classDescription.getFieldDescriptions().stream()
                .map(ClassDescription.FieldDescription::getColumnName)
                .toArray(String[]::new);
        String keyName = classDescription.getFieldDescriptions().stream()
                .filter(ClassDescription.FieldDescription::getIsKey)
                .findFirst()
                .get()
                .getColumnName();

        StringBuilder queryBuilder = new StringBuilder("select ");
        for (int i = 0; i < columnNames.length; i++) {
            if (i != 0)
                queryBuilder.append(", ");

            queryBuilder.append(columnNames[i]);
        }
        queryBuilder.append(" from ")
                .append(tableName)
                .append(" where ")
                .append(keyName)
                .append("=")
                .append(id)
                .append(";");

        return queryBuilder.toString();
    }

    public static String createInsertQuery(ObjectDescription objectDescription) {
        objectDescriptionValidator.validate(objectDescription);

        String tableName = objectDescription.getTableName();
        String[] columnNames = objectDescription.getFieldDescriptions().stream()
                .map(ObjectDescription.FieldDescription::getColumnName)
                .toArray(String[]::new);
        Object[] values = objectDescription.getFieldDescriptions().stream()
                .map(ObjectDescription.FieldDescription::getValue)
                .toArray(Object[]::new);

        StringBuilder queryBuilder = new StringBuilder("insert into ");
        queryBuilder.append(tableName)
                .append(" (");

        for (int i = 0; i < columnNames.length; i++) {
            if (i != 0)
                queryBuilder.append(", ");

            queryBuilder.append(columnNames[i]);
        }
        queryBuilder.append(") values (");

        for (int i = 0; i < columnNames.length; i++) {
            if (i != 0)
                queryBuilder.append(", ");

            queryBuilder.append(transformType(values[i]));
        }

        return queryBuilder.append(");")
                .toString();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static String createUpdateQuery(ObjectDescription objectDescription) {
        objectDescriptionValidator.validate(objectDescription);

        String tableName = objectDescription.getTableName();
        String[] columnNames = objectDescription.getFieldDescriptions().stream()
                .map(ObjectDescription.FieldDescription::getColumnName)
                .toArray(String[]::new);
        Object[] values = objectDescription.getFieldDescriptions().stream()
                .map(ObjectDescription.FieldDescription::getValue)
                .toArray(Object[]::new);
        String keyName = objectDescription.getFieldDescriptions().stream()
                .filter(ObjectDescription.FieldDescription::getIsKey)
                .findFirst()
                .get()
                .getColumnName();
        Object keyValue = objectDescription.getFieldDescriptions().stream()
                .filter(ObjectDescription.FieldDescription::getIsKey)
                .findFirst()
                .get()
                .getValue();

        StringBuilder queryBuilder = new StringBuilder("update ");
        queryBuilder.append(tableName)
                .append(" set ");

        for (int i = 0; i < columnNames.length; i++) {
            if (i != 0)
                queryBuilder.append(", ");

            queryBuilder.append(columnNames[i])
                    .append("=")
                    .append(transformType(values[i]));
        }

        return queryBuilder.append(" where ")
                .append(keyName)
                .append("=")
                .append(transformType(keyValue))
                .append(";")
                .toString();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static String createDeleteQuery(ClassDescription classDescription, Object key) {
        classDescriptionValidator.validate(classDescription);
        classDescriptionValidator.validateKey(classDescription, key);

        String tableName = classDescription.getTableName();
        String keyName = classDescription.getFieldDescriptions().stream()
                .filter(ClassDescription.FieldDescription::getIsKey)
                .findFirst()
                .get()
                .getColumnName();

        return "delete from " +
                tableName +
                " where " +
                keyName +
                "=" +
                transformType(key) +
                ";";
    }

    public static String createCreateTableQuery(ClassDescription classDescription) {
        classDescriptionValidator.validate(classDescription);

        String tableName = classDescription.getTableName();
        String[] columnNames = classDescription.getFieldDescriptions().stream()
                .map(ClassDescription.FieldDescription::getColumnName)
                .toArray(String[]::new);
        String[] columnTypes = classDescription.getFieldDescriptions().stream()
                .map(fieldDescription -> transformType(fieldDescription.getFieldClass()))
                .toArray(String[]::new);

        StringBuilder queryBuilder = new StringBuilder("create table ");
        queryBuilder.append(tableName)
                .append(" (");

        for (int i = 0; i < columnNames.length; i++) {
            if (i != 0)
                queryBuilder.append(", ");

            queryBuilder.append(columnNames[i])
                    .append(" ")
                    .append(columnTypes[i]);
        }

        queryBuilder.append(");");
        return queryBuilder.toString();
    }

    private static String transformType(Object object) {
        if (object instanceof Integer)
            return object.toString();
        if (object instanceof Double)
            return object.toString();
        if (object instanceof Float)
            return object.toString();
        if (object instanceof Long)
            return object.toString();
        if (object instanceof Short)
            return object.toString();
        else
            return "'" + object.toString() + "'";
    }

    private static String transformType(Class<?> clazz) {
        if (clazz.equals(Integer.class))
            return "integer";
        if (clazz.equals(Double.class))
            return "double";
        if (clazz.equals(Float.class))
            return "float";
        if (clazz.equals(Long.class))
            return "long";
        if (clazz.equals(Short.class))
            return "short";
        else
            return "text";
    }
}
