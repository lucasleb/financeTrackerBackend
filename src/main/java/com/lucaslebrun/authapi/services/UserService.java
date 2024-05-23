package com.lucaslebrun.authapi.services;

import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public User findByEmail(String invitedUserEmail) {
        return userRepository.findByEmail(invitedUserEmail).orElse(null);
    }

    public void deleteAccount(User user) {
        userRepository.delete(user);
    }

}