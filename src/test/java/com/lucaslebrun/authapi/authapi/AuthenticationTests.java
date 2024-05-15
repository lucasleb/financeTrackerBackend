package com.lucaslebrun.authapi.authapi;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.Test;

import com.lucaslebrun.authapi.dtos.LoginUserDto;
import com.lucaslebrun.authapi.dtos.RegisterUserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationTests {

        @Autowired
        private MockMvc mockMvc;

        @Test
        public void testSignup() throws Exception {
                RegisterUserDto userDto = new RegisterUserDto();
                userDto.setFullName("John Doe");
                userDto.setEmail("test@example.com");
                userDto.setPassword("password");

                ObjectMapper mapper = new ObjectMapper();

                mockMvc.perform(post("/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userDto)))
                                .andExpect(status().isOk())
                                .andReturn();

                LoginUserDto loginUserDto = new LoginUserDto();
                loginUserDto.setEmail("test@example.com");
                loginUserDto.setPassword("password2");

                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(loginUserDto)))
                                .andExpect(status().isUnauthorized());

                loginUserDto.setPassword("password");

                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(loginUserDto)))
                                .andExpect(status().isOk());
        }

        @Test
        public void testLogin() throws Exception {
                LoginUserDto loginUserDto = new LoginUserDto();
                loginUserDto.setEmail("notregistered@notregistered.com");
                loginUserDto.setPassword("notregistered");

                ObjectMapper mapper = new ObjectMapper();

                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(loginUserDto)))
                                .andExpect(status().isUnauthorized());

        }

}