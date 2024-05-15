package com.lucaslebrun.authapi.controllers;

import com.lucaslebrun.authapi.dtos.CreateUserGroupDto;
import com.lucaslebrun.authapi.dtos.UserDto;
import com.lucaslebrun.authapi.dtos.UserGroupDto;
import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.entities.UserGroup;
import com.lucaslebrun.authapi.services.UserGroupService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/usergroups")
public class UserGroupController {
    private final UserGroupService userGroupService;

    public UserGroupController(UserGroupService userGroupService) {
        this.userGroupService = userGroupService;
    }

    @PostMapping("")
    public ResponseEntity<UserGroupDto> createGroup(@RequestBody CreateUserGroupDto createUserGroupDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = (User) authentication.getPrincipal();

        // Create a new UserGroup
        UserGroup newGroup = new UserGroup();
        newGroup.setGroupName(createUserGroupDto.getGroupName());

        // Set the current user as the admin
        newGroup.setAdmin(currentUser);

        // Add the current user to the group's members
        newGroup.getMembers().add(currentUser);

        // Save the new group
        UserGroup savedGroup = userGroupService.save(newGroup);

        // Create a response DTO
        UserGroupDto responseDto = new UserGroupDto();
        responseDto.setId(savedGroup.getId());
        responseDto.setGroupName(savedGroup.getGroupName());

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("")
    public ResponseEntity<List<UserGroupDto>> myGroups() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = (User) authentication.getPrincipal();

        List<UserGroup> groups = userGroupService.findGroupsByMember(currentUser);

        List<UserGroupDto> userGroupDtos = groups.stream()
                .map(group -> {
                    UserGroupDto dto = new UserGroupDto();
                    dto.setId(group.getId());
                    dto.setGroupName(group.getGroupName());
                    dto.setAdmin(new UserDto(group.getAdmin()));
                    List<UserDto> memberDtos = group.getMembers().stream()
                            .map(member -> {
                                UserDto userDto = new UserDto(member);
                                return userDto;
                            })
                            .collect(Collectors.toList());
                    dto.setMembers(memberDtos);
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(userGroupDtos);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<UserGroupDto> getGroup(@PathVariable Integer groupId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<UserGroup> group = userGroupService.findGroupById(groupId);

        if (group.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<UserDto> memberDtos = group.get().getMembers().stream()
                .map(member -> {
                    UserDto userDto = new UserDto(member);
                    return userDto;
                })
                .collect(Collectors.toList());

        User currentUser = (User) authentication.getPrincipal();
        boolean isMember = memberDtos.stream().anyMatch(member -> member.getId().equals(currentUser.getId()));

        if (!isMember) {
            return ResponseEntity.status(403).build();
        }

        UserGroupDto userGroupDto = new UserGroupDto();
        userGroupDto.setId(group.get().getId());
        userGroupDto.setGroupName(group.get().getGroupName());
        userGroupDto.setMembers(memberDtos);
        userGroupDto.setAdmin(new UserDto(group.get().getAdmin()));

        return ResponseEntity.ok(userGroupDto);

    }

    @PutMapping("/{groupId}")
    public ResponseEntity<UserGroupDto> updateGroup(@PathVariable Integer groupId,
            @RequestBody CreateUserGroupDto createUserGroupDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<UserGroup> group = userGroupService.findGroupById(groupId);

        if (group.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User currentUser = (User) authentication.getPrincipal();
        UserGroup userGroup = group.get();

        // Check if the current user is the admin of the group
        if (!userGroup.getAdmin().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }

        userGroup.setGroupName(createUserGroupDto.getGroupName());

        UserGroup savedGroup = userGroupService.save(userGroup);

        UserGroupDto responseDto = new UserGroupDto();
        responseDto.setId(savedGroup.getId());
        responseDto.setGroupName(savedGroup.getGroupName());
        responseDto.setAdmin(new UserDto(savedGroup.getAdmin()));
        responseDto.setMembers(savedGroup.getMembers().stream()
                .map(member -> new UserDto(member))
                .collect(Collectors.toList()));

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Integer groupId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<UserGroup> group = userGroupService.findGroupById(groupId);

        if (group.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User currentUser = (User) authentication.getPrincipal();
        UserGroup userGroup = group.get();

        // Check if the current user is the admin of the group
        if (!userGroup.getAdmin().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }

        userGroupService.delete(userGroup);

        return ResponseEntity.noContent().build();
    }

}
