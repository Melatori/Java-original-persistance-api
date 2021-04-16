package com.things.jopa.persistance.utils;

import com.things.jopa.persistance.mapping.descriptors.ClassDescription;
import com.things.jopa.persistance.mapping.descriptors.ObjectDescription;
import com.things.jopa.testing.TestingCustomEntity;
import com.things.jopa.testing.TestingEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void shouldCreateInsertQuery() {
        //given
        String expected1 = "insert into TestingEntity (id, string, aDouble) values (1, 'test', 1.3);";
        String expected2 = "insert into Test (pk, string, aDouble) values (1, 'test', 1.3);";
        TestingEntity entity1 = TestingEntity.builder()
                .id(1)
                .string("test")
                .aDouble(1.3)
                .build();
        TestingCustomEntity entity2 = TestingCustomEntity.builder()
                .id(1)
                .string("test")
                .aDouble(1.3)
                .build();

        ObjectDescription objectDescription1 = new ObjectDescription(entity1);
        ObjectDescription objectDescription2 = new ObjectDescription(entity2);

        //when
        String actual1 = QueryCreator.createInsertQuery(objectDescription1);
        String actual2 = QueryCreator.createInsertQuery(objectDescription2);

        //then
        assertThat(actual1).isEqualTo(expected1);
        assertThat(actual2).isEqualTo(expected2);
    }

    @Test
    void shouldCreateUpdateQuery() {
        //given
        String expected1 = "update TestingEntity set id=1, string='test', aDouble=1.3 where id=1;";
        String expected2 = "update Test set pk=1, string='test', aDouble=1.3 where pk=1;";
        TestingEntity entity1 = TestingEntity.builder()
                .id(1)
                .string("test")
                .aDouble(1.3)
                .build();
        TestingCustomEntity entity2 = TestingCustomEntity.builder()
                .id(1)
                .string("test")
                .aDouble(1.3)
                .build();

        ObjectDescription objectDescription1 = new ObjectDescription(entity1);
        ObjectDescription objectDescription2 = new ObjectDescription(entity2);

        //when
        String actual1 = QueryCreator.createUpdateQuery(objectDescription1);
        String actual2 = QueryCreator.createUpdateQuery(objectDescription2);

        //then
        assertThat(actual1).isEqualTo(expected1);
        assertThat(actual2).isEqualTo(expected2);
    }

    @Test
    void shouldCreateDeleteQuery() {
        //given
        String expected1 = "delete from TestingEntity where id=1;";
        String expected2 = "delete from Test where pk=1;";

        Integer key = 1;
        ClassDescription classDescription1 = new ClassDescription(TestingEntity.class);
        ClassDescription classDescription2 = new ClassDescription(TestingCustomEntity.class);

        //when
        String actual1 = QueryCreator.createDeleteQuery(classDescription1, key);
        String actual2 = QueryCreator.createDeleteQuery(classDescription2, key);

        //then
        assertThat(actual1).isEqualTo(expected1);
        assertThat(actual2).isEqualTo(expected2);
    }

    @Test
    void shouldCreateCreateTableQuery() {
        //given
        String expected1 = "create table TestingEntity (id INTEGER PRIMARY KEY, string TEXT, aDouble DOUBLE);";
        String expected2 = "create table Test (pk INTEGER PRIMARY KEY, string TEXT, aDouble DOUBLE);";

        ClassDescription classDescription1 = new ClassDescription(TestingEntity.class);
        ClassDescription classDescription2 = new ClassDescription(TestingCustomEntity.class);

        //when
        String actual1 = QueryCreator.createCreateTableQuery(classDescription1);
        String actual2 = QueryCreator.createCreateTableQuery(classDescription2);

        //then
        assertThat(actual1).isEqualTo(expected1);
        assertThat(actual2).isEqualTo(expected2);
    }

    @Test
    void createAlterTableQuery() {
        //given
        String expected1_1 = "alter table TestingEntity drop column aDouble;";
        String expected1_2 = "alter table TestingEntity add newColumn TEXT;";
        String expected2_1 = "alter table Test drop column aDouble;";
        String expected2_2 = "alter table Test add newColumn TEXT;";

        ClassDescription classDescription1 = new ClassDescription(TestingEntity.class);
        ClassDescription classDescription2 = new ClassDescription(TestingCustomEntity.class);
        ClassDescription.FieldDescription fieldDescription = new ClassDescription.FieldDescription(
                "newColumn", "newField", String.class, false
        );

        //when
        String[] actual1 = QueryCreator.createAlterTableQuery(
                classDescription1,
                new String[]{"aDouble"},
                new ClassDescription.FieldDescription[]{fieldDescription}
        );
        String[] actual2 = QueryCreator.createAlterTableQuery(
                classDescription2,
                new String[]{"aDouble"},
                new ClassDescription.FieldDescription[]{fieldDescription}
        );

        //then
        assertThat(actual1[0]).isEqualTo(expected1_1);
        assertThat(actual1[1]).isEqualTo(expected1_2);
        assertThat(actual2[0]).isEqualTo(expected2_1);
        assertThat(actual2[1]).isEqualTo(expected2_2);
    }
}