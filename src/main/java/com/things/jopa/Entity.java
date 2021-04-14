package com.things.jopa;

import com.things.jopa.persistance.annotations.Column;
import com.things.jopa.persistance.annotations.PrimaryKey;
import lombok.Data;

@com.things.jopa.persistance.annotations.Entity
@Data
public class Entity {
    @Column
    @PrimaryKey
    Integer id;

    @Column(name = "pishpisHI")
    String string;

    @Column
    Double aDouble;
}
