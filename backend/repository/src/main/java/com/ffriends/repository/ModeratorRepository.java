package com.ffriends.repository;

import com.ffriends.domain.entity.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeratorRepository extends JpaRepository<Moderator, Long>{

    Moderator findByEmail(String email);

}
