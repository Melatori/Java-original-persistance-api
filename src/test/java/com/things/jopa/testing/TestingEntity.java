package com.things.jopa.testing;

import com.things.jopa.persistance.annotations.Column;
import com.things.jopa.persistance.annotations.Entity;
import com.things.jopa.persistance.annotations.PrimaryKey;
import lombok.Data;

@Entity
@Data
public class TestingEntity {
    @Column
    @PrimaryKey
    Integer id;

    @Column
    String string;

    @Column
    Double aDouble;
}
