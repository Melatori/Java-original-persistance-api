package com.things.jopa.persistance.validation;

import com.things.jopa.persistance.EntityInDbStatus;
import com.things.jopa.persistance.exceptions.JopaException;
import com.things.jopa.persistance.exceptions.JopaValidationException;
import com.things.jopa.persistance.mapping.descriptors.ClassDescription;
import com.things.jopa.persistance.utils.QueryCreator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.things.jopa.persistance.Messages.FETCHING_ERROR;
import static com.things.jopa.persistance.Messages.UNEXPECTED_ERROR_IN_DB;

public class SchemaValidator {
    public void validate(List<ClassDescription> entities, DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            for (ClassDescription entity : entities)
                if (validateEntity(entity, connection).equals(EntityInDbStatus.NOT_EXISTS) ||
                        validateEntity(entity, connection).equals(EntityInDbStatus.INVALID))
                    throw new JopaValidationException("Schema validation failed");
        } catch (SQLException e) {
            throw new JopaException(UNEXPECTED_ERROR_IN_DB, e);
        }
    }

    public EntityInDbStatus validateEntity(ClassDescription classDescription, Connection connection) {
        if (!isTableWithThisNameExists(classDescription.getTableName(), connection))
            return EntityInDbStatus.NOT_EXISTS;

        for (ClassDescription.FieldDescription fieldDescription : classDescription.getFieldDescriptions())
            if (isTableMatchesToField(fieldDescription, classDescription.getTableName(), connection))
                return EntityInDbStatus.INVALID;

        return EntityInDbStatus.VALID;
    }

    private boolean isTableWithThisNameExists(String tableName, Connection connection) {
        try {
            final ResultSet tables = connection.getMetaData().getTables(null, null, null, null);
            while (tables.next()) {
                final String tableNameInDb = tables.getString("TABLE_NAME");

                if (tableName.toUpperCase().equals(tableNameInDb))
                    return true;
            }
        } catch (SQLException e) {
            throw new JopaException(FETCHING_ERROR, e);
        }

        return false;
    }

    private boolean isTableMatchesToField(ClassDescription.FieldDescription fieldDescription, String tableName, Connection connection) {
        try {
            final ResultSet columns = connection.getMetaData().getColumns(null, null, tableName.toUpperCase(), null);
            while (columns.next()) {
                final String columnName = columns.getString("COLUMN_NAME");
                final String columnType = QueryCreator.transformToSqlType(columns.getInt("DATA_TYPE"));

                if (columnName.equals(fieldDescription.getColumnName().toUpperCase()))
                    return true;

                if (columnType.equals(transformType(fieldDescription.getFieldClass()).toUpperCase()))
                    return true;
            }
        } catch (SQLException e) {
            throw new JopaException(FETCHING_ERROR, e);
        }

        return false;
    }

    private String transformType(Class<?> clazz) {
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
