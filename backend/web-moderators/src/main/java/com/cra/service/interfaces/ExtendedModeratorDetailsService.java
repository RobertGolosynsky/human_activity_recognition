package com.cra.service.interfaces;

import com.cra.service.TokenUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface ExtendedModeratorDetailsService extends UserDetailsService{
    @Override
    TokenUtils.ExtendedUserDetails loadUserByUsername(String s) throws UsernameNotFoundException;
}
