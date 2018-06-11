package com.ffriends.service.impl;

import com.ffriends.domain.entity.Moderator;
import com.ffriends.factory.SecurityModeratorFactory;
import com.ffriends.repository.ModeratorRepository;
import com.ffriends.service.TokenUtils;
import com.ffriends.service.interfaces.ExtendedModeratorDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ModeratorDetailsServiceImpl implements ExtendedModeratorDetailsService {

    @Autowired
    private ModeratorRepository moderatorRepository;

    @Override
    public TokenUtils.ExtendedUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Moderator moderator = this.moderatorRepository.findByEmail(email);

        if (moderator == null) {
            throw new UsernameNotFoundException(String.format("No moderator found with email '%s'.", email));
        } else {
            return SecurityModeratorFactory.create(moderator);
        }
    }
}
