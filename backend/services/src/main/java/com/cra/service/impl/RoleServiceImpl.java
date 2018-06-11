package com.cra.service.impl;

import com.cra.domain.entity.Role;
import com.cra.domain.entity.UserRole;
import com.cra.repository.RoleRepository;
import com.cra.service.interfaces.RoleService;
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
