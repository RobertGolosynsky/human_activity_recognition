package com.ffriends.service.impl;

import com.ffriends.domain.entity.Role;
import com.ffriends.domain.entity.UserRole;
import com.ffriends.repository.RoleRepository;
import com.ffriends.service.interfaces.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role findFirstByUserRole(UserRole userRole) {
        return roleRepository.findFirstByUserRole(userRole);
    }
}
