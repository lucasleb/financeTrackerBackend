package com.lucaslebrun.authapi.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.lucaslebrun.authapi.dtos.UserGroupInvitationDto;
import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.entities.UserGroup;
import com.lucaslebrun.authapi.entities.UserGroupInvitation;
import com.lucaslebrun.authapi.services.UserGroupInvitationService;





@RestController
@RequestMapping("/invitations")
public class UserGroupInvitationController {

    private final UserGroupInvitationService userGroupInvitationService;

    public UserGroupInvitationController(UserGroupInvitationService userGroupInvitationService) {
        this.userGroupInvitationService = userGroupInvitationService;
    }

    @PostMapping("path")
    public ResponseEntity<UserGroupInvitationDto> CreateUserGroupInvitationDto (@RequestBody UserGroupInvitationDto userGroupInvitationDto) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = (User) authentication.getPrincipal();

        // check if currentUser is admin of the group he is sending a invite for
        if (!currentUser.getId().equals(userGroupInvitationDto.getGroup().getAdmin().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // // check if invited user is already a member of the group
        // if (userGroupInvitationDto.getGroup().getMembers().contains(userGroupInvitationDto.getInvitedUser())) {
        //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        // }

        // create invitation
        UserGroupInvitation userGroupInvitation = new UserGroupInvitation();

        userGroupInvitation.setAuthor(currentUser);
        userGroupInvitation.setDestinator(userGroupInvitationDto.getInvitedUser());
        userGroupInvitation.setUserGroup(userGroupInvitationDto.getGroup());

        // save invitation
        userGroupInvitation = userGroupInvitationService.save(userGroupInvitation);

        return ResponseEntity.ok(userGroupInvitationDto);
    }
    
    
}
