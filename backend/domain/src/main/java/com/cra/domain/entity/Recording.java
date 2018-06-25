package com.cra.domain.entity;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;

@Entity
public class Recording {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<GyroData> data;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar date;

    @Enumerated(EnumType.ORDINAL)
    private RecordType type;

    public Long getId() {
        return id;
    }

    public List<GyroData> getData() {
        return data;
    }

    public void setData(List<GyroData> data) {
        this.data = data;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public RecordType getType() {
        return type;
    }

    public void setType(RecordType type) {
        this.type = type;
    }
}
