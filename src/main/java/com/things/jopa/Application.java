package com.things.jopa;

import com.things.jopa.persistance.JopaInitializer;
import com.things.jopa.persistance.repository.CrudRepositoryImpl;
import com.things.jopa.persistance.utils.ConnectionHandler;
import com.things.jopa.persistance.utils.ConnectionHandlerImpl;
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

        ConnectionHandler connectionHandler = new ConnectionHandlerImpl(dataSource);

        CrudRepositoryImpl<Entity, Integer> repository = new CrudRepositoryImpl<>(Entity.class, connectionHandler);

        Entity entity = new Entity();
        entity.setId(1);
        entity.setString("ololo");
        entity.setADouble(2.4);

        System.out.println("Post: " + repository.post(entity));
        System.out.println("All: " + repository.getAll());

        entity.setString("actually new string");
        System.out.println("Put: " + repository.put(entity));
        System.out.println("All: " + repository.getAll());

        repository.delete(1);
        System.out.println("All after delete: " + repository.getAll());
    }
}
