package com.lucaslebrun.authapi.repositories;

import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.entities.UserGroup;
import com.lucaslebrun.authapi.entities.UserGroupInvitation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupInvitationRepository extends JpaRepository<UserGroupInvitation, Integer> {

    List<UserGroupInvitation> findByDestinatorEmailAndUserGroupId(String email, Integer id);

    List<UserGroupInvitation> findByDestinator(User currentUser);

    List<UserGroupInvitation> findByAuthor(User currentUser);

    Optional<UserGroupInvitation> findById(Long id);

    void deleteById(Long id);

    void deleteByUserGroup(UserGroup userGroup);

}
