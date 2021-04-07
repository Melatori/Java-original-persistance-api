package com.things.jopa.persistance.utils;

import com.things.jopa.persistance.mapping.descriptors.ClassDescription;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SchemaCreator {
    public void createSchema(List<ClassDescription> entities, DataSource dataSource) {
        try(Connection connection = dataSource.getConnection()) {
            for (ClassDescription entity : entities) {
                String query = QueryCreator.createCreateTableQuery(entity);

                final PreparedStatement statement = connection.prepareStatement(query);
                statement.execute();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
