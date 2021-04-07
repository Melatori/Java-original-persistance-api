package com.things.jopa.persistance;

import com.things.jopa.persistance.mapping.descriptors.ClassDescription;
import com.things.jopa.persistance.utils.JopaComponentStatus;
import com.things.jopa.persistance.utils.JopaComponents;
import com.things.jopa.persistance.utils.SchemaCreator;
import com.things.jopa.persistance.validation.SchemaValidator;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
public class JopaInitializer {
    public enum InitializationStrategies {
        EXISTED_SCHEMA,
        CREATE_SCHEMA
    }

    private final SchemaCreator schemaCreator = new SchemaCreator();
    private final SchemaValidator schemaValidator = new SchemaValidator();

    private InitializationStrategies strategy = InitializationStrategies.EXISTED_SCHEMA;
    private volatile JopaComponentStatus componentStatus = JopaComponentStatus.NOT_INITIALIZED;

    public synchronized void chooseStrategy(InitializationStrategies strategy) {
        if (componentStatus.isLocked()) {
            throw new IllegalStateException("Jopa is already started");
        }
        this.strategy = strategy;
    }

    public synchronized void initializeJpa(DataSource dataSource) {
        componentStatus = JopaComponentStatus.INITIALIZED;

        JopaComponents.init(dataSource);

        EntityScanner entityScanner = new EntityScanner();
        List<ClassDescription> entities = entityScanner.scan();

        if (strategy == InitializationStrategies.CREATE_SCHEMA) {
            schemaCreator.createSchema(entities, dataSource);
        }
        schemaValidator.validate(entities, dataSource);

        componentStatus = JopaComponentStatus.READY;
        log.info("Jopa is ready for adventure!");
    }
}
