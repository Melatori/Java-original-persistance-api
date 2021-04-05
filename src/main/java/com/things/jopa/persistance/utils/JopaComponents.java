package com.things.jopa.persistance.utils;

import com.things.jopa.persistance.exceptions.JopaException;

import javax.sql.DataSource;

public class JopaComponents {
    private static volatile JopaComponents instance;

    private final ConnectionHandler connectionHandler;

    private JopaComponents(DataSource dataSource) {
        connectionHandler = new ConnectionHandlerImpl(dataSource);
    }

    public static void init(DataSource dataSource) {
        instance = new JopaComponents(dataSource);
    }

    public static JopaComponents getInstance() {
        if (instance == null) {
            throw new JopaException("JopaComponents is not initialized");
        }
        return instance;
    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }
}
