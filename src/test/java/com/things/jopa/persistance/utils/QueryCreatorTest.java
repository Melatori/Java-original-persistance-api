package com.things.jopa.persistance.utils;

import com.things.jopa.persistance.mapping.descriptors.ClassDescription;
import com.things.jopa.testing.TestingCustomEntity;
import com.things.jopa.testing.TestingEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class QueryCreatorTest {

    @Test
    void shouldCreateSelectAllQuery() {
        //given
        String expected1 = "select id, string, aDouble from TestingEntity;";
        String expected2 = "select pk, string, aDouble from Test;";
        ClassDescription classDescription1 = new ClassDescription(TestingEntity.class);
        ClassDescription classDescription2 = new ClassDescription(TestingCustomEntity.class);

        //when
        String actual1 = QueryCreator.createSelectAllQuery(classDescription1);
        String actual2 = QueryCreator.createSelectAllQuery(classDescription2);

        //then
        assertThat(actual1).isEqualTo(expected1);
        assertThat(actual2).isEqualTo(expected2);
    }

    @Test
    void shouldCreateSelectByIdQuery() {
        //given
        String expected1 = "select id, string, aDouble from TestingEntity where id=1;";
        String expected2 = "select pk, string, aDouble from Test where pk=1;";
        ClassDescription classDescription1 = new ClassDescription(TestingEntity.class);
        ClassDescription classDescription2 = new ClassDescription(TestingCustomEntity.class);

        //when
        String actual1 = QueryCreator.createSelectByIdQuery(classDescription1, 1);
        String actual2 = QueryCreator.createSelectByIdQuery(classDescription2, 1);

        //then
        assertThat(actual1).isEqualTo(expected1);
        assertThat(actual2).isEqualTo(expected2);
    }
}