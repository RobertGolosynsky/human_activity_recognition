package com.ffriends.service.impl;

import com.ffriends.domain.entity.User;
import com.ffriends.factory.SecurityUserFactory;
import com.ffriends.repository.UserRepository;
import com.ffriends.service.TokenUtils;
import com.ffriends.service.interfaces.ExtendedUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements ExtendedUserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public TokenUtils.ExtendedUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with email '%s'.", email));
        } else {
            return SecurityUserFactory.create(user);
        }
    }
}
