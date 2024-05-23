package com.lucaslebrun.authapi.controllers;

import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.entities.UserGroup;
import com.lucaslebrun.authapi.services.UserService;
import com.lucaslebrun.authapi.services.UserGroupService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;
    private final UserGroupService userGroupService;

    public UserController(UserService userService, UserGroupService userGroupService) {
        this.userService = userService;
        this.userGroupService = userGroupService;
    }

    @GetMapping("/user")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser);
    }

    // @GetMapping("/all")
    // public ResponseEntity<List<User>> allUsers() {
    // List<User> users = userService.allUsers();
    // return ResponseEntity.ok(users);
    // }

    @DeleteMapping("/delete_account/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        List<UserGroup> adminGroups = userGroupService.findGroupsByAdmin(currentUser);
        for (UserGroup group : adminGroups) {
            userGroupService.delete(group);
        }

        List<UserGroup> memberGroups = userGroupService.findGroupsByMember(currentUser);
        for (UserGroup group : memberGroups) {
            group.getMembers().remove(currentUser);
            userGroupService.save(group);
        }

        userService.deleteAccount(currentUser);
        return ResponseEntity.noContent().build();
    }

}
