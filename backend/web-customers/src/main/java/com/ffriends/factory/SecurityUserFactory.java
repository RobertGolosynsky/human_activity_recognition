package com.ffriends.factory;

import com.ffriends.domain.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

public class SecurityUserFactory {

    public static SecurityUser create(User user) {
        Collection<? extends GrantedAuthority> authorities;
        try {
            authorities = AuthorityUtils.createAuthorityList(user.getRole().getUserRole().name());
        } catch (Exception e) {
            authorities = null;
        }
        return new SecurityUser(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getLastPasswordReset(),
                authorities
        );
    }

}
