package com.things.jopa.persistance.utils;

import com.things.jopa.persistance.exceptions.JopaValidationException;
import com.things.jopa.persistance.mapping.descriptors.ClassDescription;
import com.things.jopa.persistance.mapping.descriptors.ObjectDescription;
import com.things.jopa.persistance.validation.ClassDescriptionValidator;
import com.things.jopa.persistance.validation.ObjectDescriptionValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.things.jopa.persistance.utils.SqlTypesConverter.transformToSqlType;

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

            queryBuilder.append(transformToSqlType(values[i]));
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
                    .append(transformToSqlType(values[i]));
        }

        return queryBuilder.append(" where ")
                .append(keyName)
                .append("=")
                .append(transformToSqlType(keyValue))
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
                transformToSqlType(key) +
                ";";
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static String createCreateTableQuery(ClassDescription classDescription) {
        classDescriptionValidator.validate(classDescription);

        String tableName = classDescription.getTableName();
        String[] columnNames = classDescription.getFieldDescriptions().stream()
                .map(ClassDescription.FieldDescription::getColumnName)
                .toArray(String[]::new);
        String[] columnTypes = classDescription.getFieldDescriptions().stream()
                .map(fieldDescription -> transformToSqlType(fieldDescription.getFieldClass()))
                .toArray(String[]::new);
        String pkName = classDescription.getFieldDescriptions().stream()
                .filter(ClassDescription.FieldDescription::getIsKey)
                .map(ClassDescription.FieldDescription::getColumnName)
                .findFirst()
                .get();

        StringBuilder queryBuilder = new StringBuilder("create table ");
        queryBuilder.append(tableName)
                .append(" (");

        for (int i = 0; i < columnNames.length; i++) {
            if (i != 0)
                queryBuilder.append(", ");

            queryBuilder.append(columnNames[i])
                    .append(" ")
                    .append(columnTypes[i]);

            if (pkName.equals(columnNames[i]))
                queryBuilder.append(" PRIMARY KEY");
        }

        queryBuilder.append(");");
        return queryBuilder.toString();
    }

    public static String[] createAlterTableQuery(
            ClassDescription classDescription,
            String[] fieldsToRemove,
            ClassDescription.FieldDescription[] fieldsToAdd
    ) {
        classDescriptionValidator.validate(classDescription);
        if (fieldsToRemove.length < 1 && fieldsToAdd.length < 1) {
            throw new JopaValidationException("Nothing to add or remove");
        }

        List<String> resultList = new ArrayList<>();

        String tableName = classDescription.getTableName();
        String[] columnNames = Arrays.stream(fieldsToAdd)
                .map(ClassDescription.FieldDescription::getColumnName)
                .toArray(String[]::new);
        String[] columnTypes = Arrays.stream(fieldsToAdd)
                .map(fieldDescription -> transformToSqlType(fieldDescription.getFieldClass()))
                .toArray(String[]::new);

        String dropColumnQuery;
        String addColumnQuery;

        if (fieldsToRemove.length >= 1) {
            StringBuilder dropQueryBuilder = new StringBuilder("alter table ")
                    .append(tableName)
                    .append(" drop column ");
            for (int i = 0; i < fieldsToRemove.length; i++) {
                if (i != 0)
                    dropQueryBuilder.append(", ");

                dropQueryBuilder.append(fieldsToRemove[i]);
            }
            dropColumnQuery = dropQueryBuilder.append(";")
                    .toString();
            resultList.add(dropColumnQuery);
        }

        if (fieldsToAdd.length >= 1) {
            StringBuilder addQueryBuilder = new StringBuilder("alter table ")
                    .append(tableName)
                    .append(" add ");
            for (int i = 0; i < fieldsToAdd.length; i++) {
                if (i != 0)
                    addQueryBuilder.append(", ");

                addQueryBuilder.append(columnNames[i])
                        .append(" ")
                        .append(columnTypes[i]);
            }
            addColumnQuery = addQueryBuilder.append(";")
                    .toString();
            resultList.add(addColumnQuery);
        }

        return resultList.toArray(String[]::new);
    }
}
