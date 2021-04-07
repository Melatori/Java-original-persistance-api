package com.things.jopa.testing;

import com.things.jopa.persistance.annotations.Column;
import com.things.jopa.persistance.annotations.Entity;
import com.things.jopa.persistance.annotations.PrimaryKey;
import lombok.Builder;
import lombok.Data;

@Data
@Entity(name = "Test")
@Builder
public class TestingCustomEntity {
    @Column(name = "pk")
    @PrimaryKey
    Integer id;

    @Column
    String string;

    @Column
    Double aDouble;
}
