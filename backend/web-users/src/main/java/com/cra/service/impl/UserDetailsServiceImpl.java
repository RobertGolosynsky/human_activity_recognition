package com.cra.service.impl;

import com.cra.domain.entity.User;
import com.cra.factory.SecurityUser;
import com.cra.factory.SecurityUserFactory;
import com.cra.repository.UserRepository;
import com.cra.service.interfaces.ExtendedUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements ExtendedUserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public SecurityUser loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = this.userRepository.findByLogin(login);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with login '%s'.", login));
        } else {
            return SecurityUserFactory.create(user);
        }
    }
}
