package com.cra.service.impl;

import com.cra.domain.entity.Moderator;
import com.cra.factory.SecurityModeratorFactory;
import com.cra.repository.ModeratorRepository;
import com.cra.service.TokenUtils;
import com.cra.service.interfaces.ExtendedModeratorDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
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
