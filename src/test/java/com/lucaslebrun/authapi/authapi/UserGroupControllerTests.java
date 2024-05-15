package com.lucaslebrun.authapi.authapi;

import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.entities.UserGroup;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucaslebrun.authapi.dtos.CreateUserGroupDto;
import com.lucaslebrun.authapi.dtos.LoginUserDto;
import com.lucaslebrun.authapi.dtos.RegisterUserDto;
import com.lucaslebrun.authapi.services.AuthenticationService;
import com.lucaslebrun.authapi.repositories.UserGroupRepository;
import com.lucaslebrun.authapi.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static com.jayway.jsonpath.JsonPath.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
public class UserGroupControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository; // Assuming UserRepository exists

    @Autowired
    private UserGroupRepository userGroupRepository; // Assuming UserGroupRepository exists

    private User mockUser;
    private User mockUser2;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userGroupRepository.deleteAll();
        userRepository.deleteAll();
        mockUser = new User("John Doe", "test@example.com", "password");
        mockUser2 = new User("John Doe2", "test2@example.com", "password2");
    }

    // TESTS 'mygroups' endpoints
    @Test
    public void testGetGroups_AuthenticatedUser() throws Exception {
        signUpUser(mockUser);
        String jwtToken = loginUser(mockUser);

        MvcResult result = mockMvc.perform(get("/usergroups")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        assert responseContent.contains("John Doe's Personal Finances");
    }

    @Test
    public void testGetGroup_UnauthorizedUser_ForbidsAccess() throws Exception {
        // Clear authentication (simulate no user logged in)
        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/usergroups/1"))
                .andExpect(status().isForbidden()); // Expect Unauthorized status
    }

    // TESTS 'createGroup' endpoints
    @Test
    public void testCreateGroups_AuthenticatedUser() throws Exception {
        signUpUser(mockUser);
        String jwtToken = loginUser(mockUser);

        CreateUserGroupDto createGroupDto = new CreateUserGroupDto();
        createGroupDto.setGroupName("Test Group");

        MvcResult result = mockMvc.perform(post("/usergroups")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createGroupDto)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        assert responseContent.contains("Test Group");

    }

    @Test
    public void testCreateGroup_UnauthorizedUser_ForbidsCreation() throws Exception {
        // Clear authentication (simulate no user logged in)
        SecurityContextHolder.clearContext();

        CreateUserGroupDto createGroupDto = new CreateUserGroupDto();
        createGroupDto.setGroupName("Test Group");

        mockMvc.perform(post("/usergroups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createGroupDto)))
                .andExpect(status().isForbidden()); // Expect Unauthorized status
    }

    // TESTS 'getGroup' endpoints
    @Test
    public void testGetGroup_AuthenticatedUser_CanAccessOwnGroups() throws Exception {
        signUpUser(mockUser);
        String jwtToken = loginUser(mockUser);

        Integer id = getIdFirstGroup(jwtToken);

        MvcResult result = mockMvc.perform(get("/usergroups/" + id)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        assert responseContent.contains("John Doe's Personal Finances");
    }

    @Test
    public void testGetGroup_AuthenticatedUser_CannotAccessOtherUserGroups()
            throws Exception {

        userRepository.save(mockUser2);

        UserGroup userGroup = new UserGroup("John Doe2's Personal Finances", mockUser2);
        Integer id = userGroupRepository.save(userGroup).getId();

        signUpUser(mockUser);
        String jwtToken = loginUser(mockUser);

        mockMvc.perform(get("/usergroups/" + id)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());

    }

    @Test
    public void testGetGroups_UnauthorizedUser_ForbidsAccess() throws Exception {
        // Clear authentication (simulate no user logged in)
        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/usergroups"))
                .andExpect(status().isForbidden()); // Expect Unauthorized status
    }

    // TESTS 'updateGroup' endpoints
    @Test
    public void testUpdateGroup_AuthenticatedUser_CanUpdateOwnGroups() throws Exception {

        signUpUser(mockUser);
        String jwtToken = loginUser(mockUser);

        CreateUserGroupDto createGroupDto = new CreateUserGroupDto();
        createGroupDto.setGroupName("updated group name");

        Integer id = getIdFirstGroup(jwtToken);

        MvcResult result = mockMvc.perform(put("/usergroups/" + id)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createGroupDto)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        assert responseContent.contains("updated group name");

    }

    @Test
    public void testUpdateGroup_AuthenticatedUser_CannotUpdateOtherUserGroups() throws Exception {
        signUpUser(mockUser);

        userRepository.save(mockUser2);

        UserGroup userGroup = new UserGroup("John Doe2's Personal Finances", mockUser2);
        Integer id = userGroupRepository.save(userGroup).getId();

        String jwtToken = loginUser(mockUser);

        CreateUserGroupDto updateGroupDto = new CreateUserGroupDto();
        updateGroupDto.setGroupName("updated group name");

        mockMvc.perform(put("/usergroups/" + id)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateGroupDto))).andExpect(status().isForbidden());

    }

    @Test
    public void testUpdateGroup_UnauthorizedUser_ForbidsUpdate() throws Exception {
        // Clear authentication (simulate no user logged in)
        SecurityContextHolder.clearContext();

        userRepository.save(mockUser);

        UserGroup userGroup = new UserGroup("John Doe2's Personal Finances", mockUser);
        Integer id = userGroupRepository.save(userGroup).getId();

        CreateUserGroupDto updateGroupDto = new CreateUserGroupDto();
        updateGroupDto.setGroupName("updated group name");

        mockMvc.perform(put("/usergroups/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateGroupDto))).andExpect(status().isForbidden());
    }

    // TESTS 'deleteGroup' endpoints
    @Test
    public void testDeleteGroup_AuthenticatedUser_CanDeleteOwnGroups() throws Exception {

        signUpUser(mockUser);
        String jwtToken = loginUser(mockUser);

        Integer id = getIdFirstGroup(jwtToken);

        mockMvc.perform(delete("/usergroups/" + id)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

    }

    @Test
    public void testDeleteGroup_UnauthorizedUser_ForbidsDelete() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(delete("/usergroups/1"))
                .andExpect(status().isForbidden());

    }

    @Test
    public void testDeleteGroup_AuthenticatedUser_CannotDeleteOtherUserGroups() throws Exception {
        signUpUser(mockUser);

        userRepository.save(mockUser2);

        UserGroup userGroup = new UserGroup("John Doe2's Personal Finances", mockUser2);
        Integer id = userGroupRepository.save(userGroup).getId();

        String jwtToken = loginUser(mockUser);

        mockMvc.perform(delete("/usergroups/" + id)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());
    }

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
