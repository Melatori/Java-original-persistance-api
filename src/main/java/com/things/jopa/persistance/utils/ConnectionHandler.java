package com.things.jopa.persistance.utils;

import java.sql.Connection;

public interface ConnectionHandler {
    Connection getConnection();
}
