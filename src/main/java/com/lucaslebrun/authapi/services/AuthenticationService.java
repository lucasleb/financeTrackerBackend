package com.lucaslebrun.authapi.services;

import com.lucaslebrun.authapi.dtos.LoginUserDto;
import com.lucaslebrun.authapi.dtos.RegisterUserDto;
import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.entities.UserGroup;
import com.lucaslebrun.authapi.repositories.UserGroupRepository;
import com.lucaslebrun.authapi.repositories.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserGroupRepository userGroupRepository;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder, UserGroupRepository userGroupRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userGroupRepository = userGroupRepository;
    }

    public User signup(RegisterUserDto input) {
        // Step 1: Create and save the user
        User user = new User()
                .setFullName(input.getFullName())
                .setEmail(input.getEmail())
                .setPassword(passwordEncoder.encode(input.getPassword()));

        User savedUser = userRepository.save(user);

        // Step 2: Create a personal group with the user as admin and member
        UserGroup personalGroup = new UserGroup();
        personalGroup.setGroupName(savedUser.getFullName() + "'s Personal Finances");
        personalGroup.setAdmin(savedUser);

        // Add the user to the group
        personalGroup.getMembers().add(savedUser);

        // Save the personal group
        userGroupRepository.save(personalGroup);

        return savedUser; // Return the created user
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()));

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return (User) authentication.getPrincipal();
    }

}
