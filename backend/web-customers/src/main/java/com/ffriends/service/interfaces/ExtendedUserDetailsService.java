package com.ffriends.service.interfaces;

import com.ffriends.service.TokenUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface ExtendedUserDetailsService extends UserDetailsService{
    @Override
    TokenUtils.ExtendedUserDetails loadUserByUsername(String s) throws UsernameNotFoundException;
}
