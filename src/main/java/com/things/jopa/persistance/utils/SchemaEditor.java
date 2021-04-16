package com.things.jopa.persistance.utils;

import com.things.jopa.persistance.EntityInDbStatus;
import com.things.jopa.persistance.exceptions.JopaException;
import com.things.jopa.persistance.mapping.descriptors.ClassDescription;
import com.things.jopa.persistance.validation.SchemaValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.things.jopa.persistance.Messages.FETCHING_ERROR;
import static com.things.jopa.persistance.Messages.UNEXPECTED_ERROR_IN_DB;
import static com.things.jopa.persistance.utils.SqlTypesConverter.transformToSqlType;

@AllArgsConstructor
@Slf4j
public class SchemaEditor {
    private final SchemaValidator schemaValidator;

    public void createSchema(List<ClassDescription> entities, DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            for (ClassDescription entity : entities) {
                final EntityInDbStatus status = schemaValidator.validateEntity(entity, connection);

                if (status.equals(EntityInDbStatus.NOT_EXISTS)) {
                    createEntity(entity, connection);
                } else if (status.equals(EntityInDbStatus.INVALID)) {
                    updateEntity(entity, connection);
                } else if (status.equals(EntityInDbStatus.VALID)) {
                    return;
                }
            }
        } catch (SQLException e) {
            throw new JopaException(UNEXPECTED_ERROR_IN_DB, e);
        }
    }

    private void createEntity(ClassDescription entity, Connection connection) {
        try {
            String query = QueryCreator.createCreateTableQuery(entity);

            final PreparedStatement statement = connection.prepareStatement(query);
            statement.execute();
        } catch (SQLException e) {
            throw new JopaException(UNEXPECTED_ERROR_IN_DB, e);
        }
    }

    private void updateEntity(ClassDescription entity, Connection connection) {
        Map<String, String> columnMap = new HashMap<>();

        try {
            final ResultSet columns = connection.getMetaData().getColumns(null, null, entity.getTableName().toUpperCase(), null);
            while (columns.next()) {
                final String columnName = columns.getString("COLUMN_NAME");
                final String columnType = transformToSqlType(columns.getInt("DATA_TYPE"));

                columnMap.put(columnName, columnType);
            }
        } catch (SQLException e) {
            throw new JopaException(FETCHING_ERROR, e);
        }

        final ClassDescription.FieldDescription[] fieldDescriptionsForCreate = getFieldDescriptionsForCreate(entity.getFieldDescriptions(), columnMap);
        final String[] columnsNameForDelete = getColumnsNameForDelete(entity.getFieldDescriptions(), columnMap);

        if (fieldDescriptionsForCreate.length > 0 || columnsNameForDelete.length > 0)
            try {
                String[] queries = QueryCreator.createAlterTableQuery(entity, columnsNameForDelete, fieldDescriptionsForCreate);

                for (String query : queries) {
                    log.info("Query sent to db: " + query);
                    connection.prepareStatement(query).execute();
                }
            } catch (SQLException e) {
                throw new JopaException(UNEXPECTED_ERROR_IN_DB, e);
            }
    }

    private ClassDescription.FieldDescription[] getFieldDescriptionsForCreate(List<ClassDescription.FieldDescription> fieldDescriptions, Map<String, String> columnsMap) {
        return fieldDescriptions.stream()
                .filter(fieldDescription -> !((columnsMap.containsKey(fieldDescription.getColumnName().toUpperCase())) &&
                        columnsMap.containsValue(transformToSqlType(fieldDescription.getFieldClass()))))
                .toArray(ClassDescription.FieldDescription[]::new);
    }

    private String[] getColumnsNameForDelete(List<ClassDescription.FieldDescription> fieldDescriptions, Map<String, String> columnsMap) {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, String> column : columnsMap.entrySet()) {
            boolean remove = true;
            for (ClassDescription.FieldDescription fieldDescription : fieldDescriptions) {
                if (fieldDescription.getColumnName().toUpperCase().equals(column.getKey()) && transformToSqlType(fieldDescription.getFieldClass()).equals(column.getValue()))
                    remove = false;

            }
            if (remove)
                list.add(column.getKey());
        }

        return list.toArray(String[]::new);
    }
}
