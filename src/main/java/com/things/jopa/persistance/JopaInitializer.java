package com.things.jopa.persistance;

import com.things.jopa.persistance.utils.JopaComponentStatus;
import liquibase.database.Database;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class JopaInitializer {
    public enum InitializationStrategies {
        EXISTED_SCHEMA,
        CREATE_SCHEMA
    }

    private InitializationStrategies strategy = InitializationStrategies.EXISTED_SCHEMA;
    private volatile JopaComponentStatus componentStatus = JopaComponentStatus.NOT_INITIALIZED;

    public synchronized void chooseStrategy(InitializationStrategies strategy) {
        if (componentStatus.isLocked()) {
            throw new IllegalStateException("Jopa is already started");
        }
        this.strategy = strategy;
    }

    public synchronized void initializeJpa(Class<? extends Database> databaseClass) {
        //todo
        componentStatus = JopaComponentStatus.INITIALIZED;

        if (strategy == InitializationStrategies.CREATE_SCHEMA) {
            createTables(databaseClass);
        }

        componentStatus = JopaComponentStatus.READY;
        log.info("Jopa is ready for adventure!");
    }

    protected abstract void createTables(Class<? extends Database> databaseClass);
}
