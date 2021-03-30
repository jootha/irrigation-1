package org.sixdouglas.formation.spring.irrigation;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class Row {
    private int id;
    private String name;
    @Singular
    private final List<Dropper> droppers = List.of();
}
