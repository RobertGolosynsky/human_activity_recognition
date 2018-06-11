package com.cra.repository;

import com.cra.domain.entity.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeratorRepository extends JpaRepository<Moderator, Long>{

    Moderator findByEmail(String email);

}
