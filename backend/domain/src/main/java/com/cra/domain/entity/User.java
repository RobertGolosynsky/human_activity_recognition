package com.cra.domain.entity;

import com.cra.domain.base.DomainBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class User extends DomainBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String password;

    @Column
    @Email
    private String email;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Recording> recordings;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Model> models;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
