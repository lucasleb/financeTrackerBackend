package com.lucaslebrun.authapi.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.lucaslebrun.authapi.dtos.UserDto;
import com.lucaslebrun.authapi.dtos.UserGroupDto;
import com.lucaslebrun.authapi.dtos.UserGroupInvitationDto;
import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.entities.UserGroup;
import com.lucaslebrun.authapi.entities.UserGroupInvitation;
import com.lucaslebrun.authapi.dtos.UserGroupInvitationDto;
import com.lucaslebrun.authapi.services.UserGroupInvitationService;
import com.lucaslebrun.authapi.services.UserGroupService;
import com.lucaslebrun.authapi.services.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/invitations")
public class UserGroupInvitationController {

    private final UserGroupInvitationService userGroupInvitationService;

    private final UserService userService;

    private final UserGroupService userGroupService;

    public UserGroupInvitationController(UserGroupInvitationService userGroupInvitationService, UserService userService,
            UserGroupService userGroupService) {
        this.userGroupInvitationService = userGroupInvitationService;
        this.userService = userService;
        this.userGroupService = userGroupService;
    }

    @PostMapping("")
    public ResponseEntity<UserGroupInvitationDto> CreateUserGroupInvitationDto(
            @RequestBody UserGroupInvitationDto userGroupInvitationDto) {

        System.out.println(userGroupInvitationDto.getInvitedUserEmail());
        System.out.println(userGroupInvitationDto.getGroupId());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = (User) authentication.getPrincipal();

        UserGroup group = userGroupService.findGroupById(userGroupInvitationDto.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (group == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!currentUser.getId().equals(group.getAdmin().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User destinatorUser = userService.findByEmail(userGroupInvitationDto.getInvitedUserEmail());

        if (destinatorUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (group.getMembers().contains(destinatorUser)) {
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = (User) authentication.getPrincipal();

        List<UserGroupInvitation> invitations = userGroupInvitationService.findByDestinator(currentUser);

        return ResponseEntity.ok(invitations.stream()
                .map(invitation -> {
                    UserGroupInvitationDto dto = new UserGroupInvitationDto();
                    dto.setInvitationId(invitation.getId());
                    dto.setGroupName(invitation.getUserGroup().getGroupName());
                    dto.setInvitedUserEmail(invitation.getDestinator().getEmail());
                    dto.setGroupId(invitation.getUserGroup().getId());
                    return dto;
                }).collect(Collectors.toList()));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<UserGroupInvitationDto>> pendingInvitations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = (User) authentication.getPrincipal();

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
    public ResponseEntity<Void> acceptInvitation(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = (User) authentication.getPrincipal();

        Optional<UserGroupInvitation> invitation = userGroupInvitationService.findById(id);

        if (invitation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserGroupInvitation invitationObj = invitation.get();

        if (!currentUser.getId().equals(invitationObj.getDestinator().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        invitationObj.getUserGroup().addMember(currentUser);
        userGroupService.save(invitationObj.getUserGroup());

        // delete the invitation
        userGroupInvitationService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @Transactional
    public void someServiceMethod(User currentUser, UserGroupInvitation invitationObj) {
        System.out.println("invitationObj " + invitationObj);

        // find the group associated with the invitation
        UserGroup group = userGroupInvitationService.getGroupFromInvitation(invitationObj);
        System.out.println("group yo yo " + group);
        group.addMember(currentUser);
        userGroupService.save(group);
    }

    // refuse invitation

    @DeleteMapping("/{id}/refuse")
    public ResponseEntity<Void> refuseInvitation(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = (User) authentication.getPrincipal();

        Optional<UserGroupInvitation> invitation = userGroupInvitationService.findById(id);

        if (invitation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserGroupInvitation invitationObj = invitation.get();

        if (!currentUser.getId().equals(invitationObj.getDestinator().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userGroupInvitationService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // delete invitation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvitation(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = (User) authentication.getPrincipal();

        Optional<UserGroupInvitation> invitation = userGroupInvitationService.findById(id);

        if (invitation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserGroupInvitation invitationObj = invitation.get();

        if (!currentUser.getId().equals(invitationObj.getAuthor().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userGroupInvitationService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
