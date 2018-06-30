package com.cra.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Blob;
import java.util.Calendar;

@Entity
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class Model {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private double score;

    @Column
    private java.lang.String classifierName;

    @Column(columnDefinition="DATETIME(3)")
    private Calendar date;

    @Column
    private Blob classifier;

    public Model(double score, java.lang.String classifierName, Calendar date, Blob
            classifier) {
        this.score = score;
        this.classifierName = classifierName;
        this.date = date;
        this.classifier = classifier;
    }
}
