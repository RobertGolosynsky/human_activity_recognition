package com.cra.repository;

import com.cra.domain.entity.Role;
import com.cra.domain.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findFirstByUserRole(UserRole userRole);

}
