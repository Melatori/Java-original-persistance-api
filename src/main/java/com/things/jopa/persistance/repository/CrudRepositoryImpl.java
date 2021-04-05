package com.things.jopa.persistance.repository;

import com.things.jopa.persistance.exceptions.JopaException;
import com.things.jopa.persistance.mapping.ObjectMapperImpl;
import com.things.jopa.persistance.mapping.descriptors.ClassDescription;
import com.things.jopa.persistance.mapping.descriptors.ObjectDescription;
import com.things.jopa.persistance.mapping.ObjectMapper;
import com.things.jopa.persistance.utils.ConnectionHandler;
import com.things.jopa.persistance.utils.QueryCreator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CrudRepositoryImpl<T, K> implements CrudRepository<T, K>{
    private final Class<T> tClass;
    private final ConnectionHandler connectionHandler;
    private final ObjectMapper<T> objectMapper;
    private final ClassDescription classDescription;

    public CrudRepositoryImpl(Class<T> tClass, ConnectionHandler connectionHandler) {
        this.tClass = tClass;
        this.connectionHandler = connectionHandler;

        objectMapper = new ObjectMapperImpl<>();
        classDescription = new ClassDescription(tClass);
    }

    @Override
    public Collection<T> getAll() {
        ResultSet resultSet;
        List<T> result = new ArrayList<>();

        String query = QueryCreator.createSelectAllQuery(classDescription);
        Connection connection = connectionHandler.getConnection();

        try (Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            throw new JopaException("Statement failed exception", e);
        }
        if (resultSet == null) {
            return result;
        }

        List<ObjectDescription> resultDescriptors = new ArrayList<>();
        try {
            while (resultSet.next()) {
                ObjectDescription resultDescriptor = new ObjectDescription(resultSet, tClass);
                resultDescriptors.add(resultDescriptor);
            }
        } catch (SQLException e) {
            throw new JopaException("ResultSet exception", e);
        }

        for (ObjectDescription descriptor : resultDescriptors) {
            result.add(objectMapper.convertToObject(descriptor));
        }

        return result;
    }

    @Override
    public T getById(K id) {
        ResultSet resultSet;

        String query = QueryCreator.createSelectByIdQuery(classDescription, id);
        Connection connection = connectionHandler.getConnection();

        try (Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            throw new JopaException("Statement failed exception", e);
        }
        if (resultSet == null) {
            return null;
        }

        try {
            resultSet.next();
        } catch (SQLException e) {
            throw new JopaException("ResultSet exception", e);
        }

        ObjectDescription resultDescriptor = new ObjectDescription(resultSet, tClass);

        return objectMapper.convertToObject(resultDescriptor);
    }

    @Override
    public T post(T object) {
        return null;
    }

    @Override
    public T put(T object) {
        return null;
    }

    @Override
    public void delete(K key) {

    }
}
