package com.crediya.r2dbc.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("estados")
public class StateEntity {
    @Id
    private Integer stateId;
    private String name;
    private String description;
}

