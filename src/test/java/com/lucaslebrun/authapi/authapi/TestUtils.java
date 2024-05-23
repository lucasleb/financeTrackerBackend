package com.lucaslebrun.authapi.authapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import static com.jayway.jsonpath.JsonPath.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.repositories.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TestUtils {

    private final MockMvc mockMvc;
    private final UserRepository userRepository;

    public TestUtils(MockMvc mockMvc, UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
    }

    public String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User signUpUser(User user) throws Exception {
        String response = mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andReturn().getResponse().getContentAsString();

        // to do: conversion from string to int ?
        Integer userId = read(response, "$.id");

        User registeredUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found"));

        return registeredUser;

    }

    public String loginUser(User user) throws Exception {
        String token = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andReturn().getResponse().getContentAsString();

        String jwtToken = read(token, "$.token");

        return jwtToken;
    }

    public Integer getIdFirstGroup(String jwtToken) throws Exception {
        MvcResult resultt = mockMvc.perform(get("/usergroups")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn();

        String responseContentt = resultt.getResponse().getContentAsString();
        int id = read(responseContentt, "$[0].id");

        return id;
    }
}
