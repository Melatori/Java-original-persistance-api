package com.things.jopa.persistance.utils;

import com.things.jopa.persistance.mapping.descriptors.ClassDescription;
import com.things.jopa.persistance.validation.ClassDescriptionValidator;

public class QueryCreator {
    private static final ClassDescriptionValidator classDescriptionValidator = new ClassDescriptionValidator();

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
}
