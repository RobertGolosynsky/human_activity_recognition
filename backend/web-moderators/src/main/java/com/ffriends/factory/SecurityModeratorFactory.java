package com.ffriends.factory;

import com.ffriends.domain.entity.Moderator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

public class SecurityModeratorFactory {

    public static SecurityModerator create(Moderator moderator) {
        Collection<? extends GrantedAuthority> authorities;
        try {
            authorities = AuthorityUtils.createAuthorityList(moderator.getRole().getUserRole().toString());
        } catch (Exception e) {

            authorities = null;
        }
        return new SecurityModerator(
                moderator.getId(),
                moderator.getEmail(),
                moderator.getPassword(),
                moderator.getLastPasswordReset(),
                authorities
        );
    }

}
