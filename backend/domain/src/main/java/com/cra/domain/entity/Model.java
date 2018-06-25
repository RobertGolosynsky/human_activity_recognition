package com.cra.domain.entity;

import javax.persistence.*;
import java.util.Calendar;

@Entity
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

    public Long getId() {
        return id;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }
}
