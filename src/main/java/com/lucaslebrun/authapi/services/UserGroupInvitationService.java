package com.lucaslebrun.authapi.services;

import com.lucaslebrun.authapi.entities.UserGroupInvitation;
import com.lucaslebrun.authapi.repositories.UserGroupInvitationRepository;

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
}
