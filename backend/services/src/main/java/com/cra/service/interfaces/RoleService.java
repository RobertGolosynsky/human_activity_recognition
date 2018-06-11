package com.cra.service.interfaces;

import com.cra.domain.entity.Role;
import com.cra.domain.entity.UserRole;

public interface RoleService {

    Role findFirstByUserRole(UserRole userRole);

}
