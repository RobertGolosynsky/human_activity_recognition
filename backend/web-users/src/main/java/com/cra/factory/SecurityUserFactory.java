package com.cra.factory;

import com.cra.domain.entity.User;

public class SecurityUserFactory {

    public static SecurityUser create(User user) {
        return new SecurityUser(
                user.getId(),
                user.getLogin(),
                user.getPassword()
        );
    }

}
