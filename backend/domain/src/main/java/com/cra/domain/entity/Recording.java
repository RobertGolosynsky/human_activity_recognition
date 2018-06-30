package com.cra.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;

@Entity
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class Recording {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<GyroData> data;

    //@Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition="DATETIME(3)")
    private Calendar date;

    @Enumerated(EnumType.ORDINAL)
    private RecordType type;

    @Column
    private Long duration;

}
