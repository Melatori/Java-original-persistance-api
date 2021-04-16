package com.things.jopa.persistance.utils;

import com.things.jopa.persistance.exceptions.JopaException;

public class SqlTypesConverter {
    public static String transformToSqlType(Object object) {
        if (object instanceof Integer)
            return object.toString();
        if (object instanceof Double)
            return object.toString();
        if (object instanceof Float)
            return object.toString();
        if (object instanceof Long)
            return object.toString();
        if (object instanceof Short)
            return object.toString();
        else
            return "'" + object.toString() + "'";
    }

    public static String transformToSqlType(Class<?> clazz) {
        if (clazz.equals(Integer.class))
            return "INTEGER";
        if (clazz.equals(Double.class))
            return "DOUBLE";
        if (clazz.equals(Float.class))
            return "FLOAT";
        if (clazz.equals(Long.class))
            return "LONG";
        if (clazz.equals(Short.class))
            return "SHORT";
        else
            return "TEXT";
    }

    public static String transformToSqlType(int javaSQLType) {
        return switch (javaSQLType) {
            case 4 -> "INTEGER";
            case 8 -> "DOUBLE";
            case 6 -> "FLOAT";
            case -5 -> "LONG";
            case 5 -> "SHORT";
            case 2005 -> "TEXT";
            default -> throw new JopaException("Unrecognized javaSQLType: " + javaSQLType);
        };
    }
}
