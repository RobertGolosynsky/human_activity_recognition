package com.cra.service.interfaces;

import com.cra.factory.SecurityUser;
import com.cra.service.TokenUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface ExtendedUserDetailsService extends UserDetailsService{
    @Override
    SecurityUser loadUserByUsername(String s) throws UsernameNotFoundException;
}
