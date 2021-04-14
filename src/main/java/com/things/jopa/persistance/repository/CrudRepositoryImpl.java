package com.things.jopa.persistance.repository;

import com.things.jopa.persistance.exceptions.JopaException;
import com.things.jopa.persistance.mapping.ObjectMapper;
import com.things.jopa.persistance.mapping.ObjectMapperImpl;
import com.things.jopa.persistance.mapping.descriptors.ClassDescription;
import com.things.jopa.persistance.mapping.descriptors.ObjectDescription;
import com.things.jopa.persistance.utils.ConnectionHandler;
import com.things.jopa.persistance.utils.QueryCreator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.things.jopa.persistance.Messages.UNEXPECTED_ERROR_IN_DB;

public class CrudRepositoryImpl<T, K> implements CrudRepository<T, K> {
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

        try (Connection connection = connectionHandler.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);

            resultSet = statement.executeQuery();

            if (resultSet == null) {
                return result;
            }

            List<ObjectDescription> resultDescriptors = new ArrayList<>();
            while (resultSet.next()) {
                ObjectDescription resultDescriptor = new ObjectDescription(resultSet, tClass);
                resultDescriptors.add(resultDescriptor);
            }
            for (ObjectDescription descriptor : resultDescriptors) {
                result.add(objectMapper.convertToObject(descriptor));
            }
        } catch (SQLException e) {
            throw new JopaException(UNEXPECTED_ERROR_IN_DB, e);
        }
        return result;
    }

    @Override
    public T getById(K id) {
        ResultSet resultSet;

        String query = QueryCreator.createSelectByIdQuery(classDescription, id);

        try (Connection connection = connectionHandler.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);

            resultSet = statement.executeQuery();

            if (resultSet == null) {
                return null;
            }

            ObjectDescription resultDescriptor = null;
            while (resultSet.next()) {
                resultDescriptor = new ObjectDescription(resultSet, tClass);
            }
            if (resultDescriptor == null)
                return null;

            return objectMapper.convertToObject(resultDescriptor);
        } catch (SQLException e) {
            throw new JopaException(UNEXPECTED_ERROR_IN_DB, e);
        }
    }

    @Override
    public T post(T object) {
        ObjectDescription objectDescription = new ObjectDescription(object);
        String query = QueryCreator.createInsertQuery(objectDescription);

        try (Connection connection = connectionHandler.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();

            //noinspection unchecked
            return getById((K) objectDescription.getId());
        } catch (SQLException e) {
            throw new JopaException(UNEXPECTED_ERROR_IN_DB, e);
        }
    }

    @Override
    public T put(T object) {
        ObjectDescription objectDescription = new ObjectDescription(object);
        String query = QueryCreator.createUpdateQuery(objectDescription);

        try (Connection connection = connectionHandler.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();

            //noinspection unchecked
            return getById((K) objectDescription.getId());
        } catch (SQLException e) {
            throw new JopaException(UNEXPECTED_ERROR_IN_DB, e);
        }
    }

    @Override
    public void delete(K key) {
        String query = QueryCreator.createDeleteQuery(classDescription, key);

        try (Connection connection = connectionHandler.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new JopaException(UNEXPECTED_ERROR_IN_DB, e);
        }
    }
}
