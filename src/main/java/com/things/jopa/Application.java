package com.things.jopa;

import com.things.jopa.persistance.JopaInitializer;
import com.things.jopa.persistance.JopaLiquibaseInitializer;
import com.things.jopa.persistance.annotations.Entity;
import com.things.jopa.persistance.utils.JopaComponents;
import liquibase.database.core.H2Database;
import org.h2.jdbcx.JdbcDataSource;

import static com.things.jopa.persistance.JopaInitializer.InitializationStrategies.CREATE_SCHEMA;

public class Application {
    public static void main(String[] args) {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:˜/test");
        dataSource.setUser("admin");
        dataSource.setPassword("admin");

        JopaInitializer jopaInitializer = new JopaLiquibaseInitializer();
        jopaInitializer.chooseStrategy(CREATE_SCHEMA);
        jopaInitializer.initializeJpa(H2Database.class);

        JopaComponents.init(dataSource);
    }
}
