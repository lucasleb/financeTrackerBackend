package com.lucaslebrun.authapi.services;

import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }

    public Optional<User> findByEmailWithGroups(String email) {
        return userRepository.findByEmailWithGroups(email);
    }
}