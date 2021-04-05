package com.things.jopa.persistance.utils;

import com.things.jopa.persistance.exceptions.JopaException;
import lombok.AllArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class ConnectionHandlerImpl implements ConnectionHandler {
    private final DataSource dataSource;

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new JopaException("Unable to get connection");
        }
    }
}
