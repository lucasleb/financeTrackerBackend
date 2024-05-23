package com.lucaslebrun.authapi.authapi;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lucaslebrun.authapi.dtos.LoginUserDto;
import com.lucaslebrun.authapi.dtos.RegisterUserDto;
import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.repositories.UserGroupRepository;
import com.lucaslebrun.authapi.repositories.UserRepository;
import com.lucaslebrun.authapi.services.AuthenticationService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationTests {

        @Autowired
        private MockMvc mockMvc;

        @Mock
        private AuthenticationService authenticationService;

        @Autowired
        private UserRepository userRepository; // Assuming UserRepository exists

        @Autowired
        private UserGroupRepository userGroupRepository; // Assuming UserGroupService exists

        private User mockUser;
        // private User mockUser2;
        private TestUtils testUtils;

        @BeforeEach
        public void setup() {
                MockitoAnnotations.openMocks(this);
                userGroupRepository.deleteAll();
                userRepository.deleteAll();
                mockUser = new User("John Doe", "test@example.com", "password");
                // mockUser2 = new User("John Doe2", "test2@example.com", "password2");
                testUtils = new TestUtils(mockMvc, userRepository);

        }

        @Test
        public void signup() throws Exception {
                RegisterUserDto userDto = new RegisterUserDto("John Doe", "test@example.com", "password");

                mockMvc.perform(post("/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(testUtils.asJsonString(userDto)))
                                .andExpect(status().isOk());
        }

        @Test
        public void shouldReturnUnauthorizedForNonExistingUser() throws Exception {
                LoginUserDto loginUserDto = new LoginUserDto();
                loginUserDto.setEmail("notregistered@notregistered.com");
                loginUserDto.setPassword("notregistered");

                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(testUtils.asJsonString(loginUserDto)))
                                .andExpect(status().isUnauthorized());

        }

        @Test
        public void shouldReturnOkForExistingUser() throws Exception {
                testUtils.signUpUser(mockUser);

                LoginUserDto loginUserDto = new LoginUserDto(mockUser.getEmail(), mockUser.getPassword());

                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(testUtils.asJsonString(loginUserDto)))
                                .andExpect(status().isOk());

        }
}