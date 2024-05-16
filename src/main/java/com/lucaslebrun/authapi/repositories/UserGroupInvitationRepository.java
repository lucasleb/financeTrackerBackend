package com.lucaslebrun.authapi.repositories;

import com.lucaslebrun.authapi.entities.UserGroupInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupInvitationRepository extends JpaRepository<UserGroupInvitation, Integer>{
    
}
