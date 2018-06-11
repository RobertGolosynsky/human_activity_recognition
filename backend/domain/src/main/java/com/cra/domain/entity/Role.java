package com.cra.domain.entity;

import com.cra.domain.base.DomainBase;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Role extends DomainBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public Role() {
    }

    public Role(UserRole userRole) {
        this.userRole = userRole;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
