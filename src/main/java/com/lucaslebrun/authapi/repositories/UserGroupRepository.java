package com.lucaslebrun.authapi.repositories;

import com.lucaslebrun.authapi.entities.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lucaslebrun.authapi.entities.User;

import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroup, Integer> {
    List<UserGroup> findByMembersContaining(User user);

    List<UserGroup> findByAdmin(User user);

    List<UserGroup> findByMembers(User user);
}
