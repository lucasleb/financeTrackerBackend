package com.lucaslebrun.authapi.authapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.jayway.jsonpath.JsonPath.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucaslebrun.authapi.dtos.CreateUserGroupDto;
import com.lucaslebrun.authapi.dtos.LoginUserDto;
import com.lucaslebrun.authapi.dtos.RegisterUserDto;
import com.lucaslebrun.authapi.dtos.UserGroupInvitationDto;
import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.entities.UserGroup;
import com.lucaslebrun.authapi.repositories.UserRepository;

import com.lucaslebrun.authapi.services.AuthenticationService;
import com.lucaslebrun.authapi.services.UserGroupService;




@SpringBootTest
@AutoConfigureMockMvc
public class UserGroupInvitationTests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository; // Assuming UserRepository exists

    @Autowired
    private UserGroupService userGroupRepository; // Assuming UserGroupService exists

    private User mockUser;
    private User mockUser2;

        @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userRepository.deleteAll();
        mockUser = new User("John Doe", "test@example.com", "password");
        mockUser2 = new User("John Doe2", "test2@example.com", "password2");
    }

    //     @Test
    // public void testCreateUserGroupInvitation_AuthenticatedUser() throws Exception {
    //     signUpUser(mockUser);
    //     String jwtToken = loginUser(mockUser);



    //     // create a group whose admin is the authenticated user
    //     UserGroup userGroup = new UserGroup();
    //     userGroup.setGroupName("Group 1");

    //     userGroup.setAdmin(mockUser);
    //     userGroup.getMembers().add(mockUser); // Fix: Pass a Set<User> instead of a single User object
    //     userGroupRepository.save(userGroup);

    //     userRepository.save(mockUser2);


    //     UserGroupInvitationDto userGroupInvitationDto = new UserGroupInvitationDto();
    //     userGroupInvitationDto.setInvitedUserEmail(mockUser2.getEmail());
    //     userGroupInvitationDto.setGroup(new UserGroup());


    //     mockMvc.perform(post("/invitations")
    //             .header("Authorization", "Bearer " + jwtToken)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(asJsonString(userGroupInvitationDto)))
    //             .andExpect(status().isOk());

    // }

        private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void signUpUser(User user) throws Exception {
        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(user);

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)));
    }

    private String loginUser(User user) throws Exception {
        when(authenticationService.authenticate(any(LoginUserDto.class))).thenReturn(user);

        String token = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andReturn().getResponse().getContentAsString();

        String jwtToken = read(token, "$.token");

        return jwtToken;
    }

    private Integer getIdFirstGroup(String jwtToken) throws Exception {
        MvcResult resultt = mockMvc.perform(get("/usergroups")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn();

        String responseContentt = resultt.getResponse().getContentAsString();
        int id = read(responseContentt, "$[0].id");

        return id;
    }
    
}
