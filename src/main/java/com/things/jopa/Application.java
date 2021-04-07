package com.things.jopa;

import com.things.jopa.persistance.JopaInitializer;
import org.h2.jdbcx.JdbcDataSource;

public class Application {
    public static void main(String[] args) {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:~/test");
        dataSource.setUser("sa");
        dataSource.setPassword("");


        JopaInitializer jopaInitializer = new JopaInitializer();
        jopaInitializer.chooseStrategy(JopaInitializer.InitializationStrategies.CREATE_SCHEMA);
        jopaInitializer.initializeJpa(dataSource);
    }
}
