package com.ffriends.service.interfaces;

import com.ffriends.domain.entity.Role;
import com.ffriends.domain.entity.UserRole;

public interface RoleService {

    Role findFirstByUserRole(UserRole userRole);

}
