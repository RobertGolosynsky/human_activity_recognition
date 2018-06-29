package com.cra.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String password;

    @Column(unique = true)
    private String login;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Recording> recordings;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Model> models;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
