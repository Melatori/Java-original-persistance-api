package com.things.jopa;

import com.things.jopa.persistance.annotations.Column;
import com.things.jopa.persistance.annotations.PrimaryKey;

@com.things.jopa.persistance.annotations.Entity
public class Entity {
    @Column
    @PrimaryKey
    Integer id;

    @Column(name = "pishpisH")
    String string;

    @Column
    Double aDouble;
}
