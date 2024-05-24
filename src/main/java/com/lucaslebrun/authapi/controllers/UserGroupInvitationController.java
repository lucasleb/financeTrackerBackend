package com.lucaslebrun.authapi.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.lucaslebrun.authapi.dtos.UserGroupInvitationDto;
import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.entities.UserGroup;
import com.lucaslebrun.authapi.entities.UserGroupInvitation;
import com.lucaslebrun.authapi.services.AuthenticationService;
import com.lucaslebrun.authapi.services.UserGroupInvitationService;
import com.lucaslebrun.authapi.services.UserGroupService;
import com.lucaslebrun.authapi.services.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/invitations")
public class UserGroupInvitationController {

    private final UserGroupInvitationService userGroupInvitationService;
    private final UserService userService;
    private final UserGroupService userGroupService;
    private final AuthenticationService authenticationService;

    public UserGroupInvitationController(UserGroupInvitationService userGroupInvitationService, UserService userService,
            UserGroupService userGroupService, AuthenticationService authenticationService) {
        this.userGroupInvitationService = userGroupInvitationService;
        this.userService = userService;
        this.userGroupService = userGroupService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("")
    public ResponseEntity<UserGroupInvitationDto> CreateUserGroupInvitationDto(
            @RequestBody UserGroupInvitationDto userGroupInvitationDto) {

        User currentUser = authenticationService.getCurrentUser();

        UserGroup group = userGroupService.findGroupById(userGroupInvitationDto.getGroupId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));

        if (!currentUser.getId().equals(group.getAdmin().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User destinatorUser = userService.findByEmail(userGroupInvitationDto.getInvitedUserEmail());

        if (destinatorUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (group.getMembers().stream().anyMatch(member -> member.getId().equals(destinatorUser.getId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<UserGroupInvitation> existingInvitations = userGroupInvitationService
                .findByDestinatorEmailAndUserGroupId(destinatorUser, group);

        if (!existingInvitations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserGroupInvitation userGroupInvitation = new UserGroupInvitation();
        userGroupInvitation.setAuthor(currentUser);
        userGroupInvitation.setDestinator(destinatorUser);
        userGroupInvitation.setUserGroup(group);
        userGroupInvitation = userGroupInvitationService.save(userGroupInvitation);

        userGroupInvitationDto.setGroupName(group.getGroupName());
        userGroupInvitationDto.setInvitationId(userGroupInvitation.getId());
        return ResponseEntity.ok(userGroupInvitationDto);
    }

    @GetMapping("")
    public ResponseEntity<List<UserGroupInvitationDto>> myInvitations() {
        User currentUser = authenticationService.getCurrentUser();

        List<UserGroupInvitation> invitations = userGroupInvitationService.findByDestinator(currentUser);

        return ResponseEntity.ok(invitations.stream()
                .map(invitation -> {
                    UserGroupInvitationDto dto = new UserGroupInvitationDto();
                    dto.setInvitationId(invitation.getId());
                    dto.setGroupName(invitation.getUserGroup().getGroupName());
                    dto.setInvitedUserEmail(invitation.getDestinator().getEmail());
                    dto.setGroupId(invitation.getUserGroup().getId());
                    dto.setAuthorEmail(invitation.getAuthor().getEmail());
                    return dto;
                }).collect(Collectors.toList()));

    }

    @GetMapping("/pending")
    public ResponseEntity<List<UserGroupInvitationDto>> pendingSentInvitations() {
        User currentUser = authenticationService.getCurrentUser();

        List<UserGroupInvitation> invitations = userGroupInvitationService.findByAuthor(currentUser);

        return ResponseEntity.ok(invitations.stream()
                .map(invitation -> {
                    UserGroupInvitationDto dto = new UserGroupInvitationDto();
                    dto.setInvitationId(invitation.getId());
                    dto.setInvitedUserEmail(invitation.getDestinator().getEmail());
                    dto.setGroupId(invitation.getUserGroup().getId());
                    dto.setGroupName(invitation.getUserGroup().getGroupName());
                    return dto;
                }).collect(Collectors.toList()));

    }

    // accept invitation
    @DeleteMapping("/{id}/accept")
    public ResponseEntity<Void> acceptInvitation(@PathVariable Integer id) {
        System.out.println("Accepting invitation");
        User currentUser = authenticationService.getCurrentUser();

        UserGroupInvitation invitation = findInvitationById(id);

        if (!currentUser.getId().equals(invitation.getDestinator().getId())) {
            System.out.println("User not the destinator of the invitation");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // TODO: remove the eager loading of members of the group
        invitation.getUserGroup().addMember(currentUser);
        userGroupService.save(invitation.getUserGroup());

        // delete the invitation
        userGroupInvitationService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // refuse invitation

    @DeleteMapping("/{id}/decline")
    public ResponseEntity<Void> declineInvitation(@PathVariable Integer id) {
        User currentUser = authenticationService.getCurrentUser();

        UserGroupInvitation invitation = findInvitationById(id);

        if (!currentUser.getId().equals(invitation.getDestinator().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userGroupInvitationService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // delete invitation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvitation(@PathVariable Integer id) {
        User currentUser = authenticationService.getCurrentUser();

        UserGroupInvitation invitation = findInvitationById(id);

        if (!currentUser.getId().equals(invitation.getAuthor().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userGroupInvitationService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private UserGroupInvitation findInvitationById(Integer id) {

        return userGroupInvitationService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found"));

    }

}
