package com.cra.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Getter
@EqualsAndHashCode
public class Model {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private float score;

    @Column
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar date;

}
