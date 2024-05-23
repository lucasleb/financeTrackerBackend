package com.lucaslebrun.authapi.services;

import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.entities.UserGroup;
import com.lucaslebrun.authapi.entities.UserGroupInvitation;

import com.lucaslebrun.authapi.repositories.UserGroupInvitationRepository;

import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserGroupInvitationService {
    private final UserGroupInvitationRepository userGroupInvitationRepository;

    public UserGroupInvitationService(UserGroupInvitationRepository userGroupInvitationRepository) {
        this.userGroupInvitationRepository = userGroupInvitationRepository;
    }

    public void deleteById(Integer invitationId) {
        userGroupInvitationRepository.deleteById(invitationId);
    }

    public UserGroupInvitation save(UserGroupInvitation userGroupInvitation) {
        return userGroupInvitationRepository.save(userGroupInvitation);
    }

    public List<UserGroupInvitation> findByDestinatorEmailAndUserGroupId(User destinatorUser, UserGroup group) {
        return userGroupInvitationRepository.findByDestinatorEmailAndUserGroupId(destinatorUser.getEmail(),
                group.getId());
    }

    public List<UserGroupInvitation> findByDestinator(User currentUser) {
        return userGroupInvitationRepository.findByDestinator(currentUser);
    }

    public List<UserGroupInvitation> findByAuthor(User currentUser) {
        return userGroupInvitationRepository.findByAuthor(currentUser);
    }

    public Optional<UserGroupInvitation> findById(Long id) {
        Optional<UserGroupInvitation> invitation = userGroupInvitationRepository.findById(id);
        invitation.ifPresent(destinator -> Hibernate.initialize(destinator.getDestinator()));
        return invitation;
    }

    public void deleteById(Long id) {
        userGroupInvitationRepository.deleteById(id);
    }

}
