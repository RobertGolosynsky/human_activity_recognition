package com.ffriends.repository;

import com.ffriends.domain.entity.Role;
import com.ffriends.domain.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findFirstByUserRole(UserRole userRole);

}
