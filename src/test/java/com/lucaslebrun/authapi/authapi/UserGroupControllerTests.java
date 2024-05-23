package com.lucaslebrun.authapi.authapi;

import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.entities.UserGroup;
import com.lucaslebrun.authapi.dtos.CreateUserGroupDto;

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
    private TestUtils testUtils;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userGroupRepository.deleteAll();
        userRepository.deleteAll();
        mockUser = new User("John Doe", "test@example.com", "password");
        mockUser2 = new User("John Doe2", "test2@example.com", "password2");
        testUtils = new TestUtils(mockMvc, userRepository);
    }

    // TESTS 'mygroups' endpoints
    @Test
    public void testGetGroups_AuthenticatedUser() throws Exception {
        testUtils.signUpUser(mockUser);
        String jwtToken = testUtils.loginUser(mockUser);

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
        testUtils.signUpUser(mockUser);
        String jwtToken = testUtils.loginUser(mockUser);

        CreateUserGroupDto createGroupDto = new CreateUserGroupDto();
        createGroupDto.setGroupName("Test Group");

        MvcResult result = mockMvc.perform(post("/usergroups")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testUtils.asJsonString(createGroupDto)))
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
                .content(testUtils.asJsonString(createGroupDto)))
                .andExpect(status().isForbidden()); // Expect Unauthorized status
    }

    // TESTS 'getGroup' endpoints
    @Test
    public void testGetGroup_AuthenticatedUser_CanAccessOwnGroups() throws Exception {
        User registeredUser = testUtils.signUpUser(mockUser);
        String jwtToken = testUtils.loginUser(mockUser);

        UserGroup group = userGroupRepository.save(new UserGroup("test", registeredUser));

        MvcResult result = mockMvc.perform(get("/usergroups/" + group.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        assert responseContent.contains("test");
    }

    @Test
    public void testGetGroup_AuthenticatedUser_CannotAccessOtherUserGroups()
            throws Exception {

        userRepository.save(mockUser2);

        UserGroup group = userGroupRepository.save(new UserGroup("test", mockUser2));

        testUtils.signUpUser(mockUser);
        String jwtToken = testUtils.loginUser(mockUser);

        mockMvc.perform(get("/usergroups/" + group.getId())
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

        User registeredUser = testUtils.signUpUser(mockUser);
        String jwtToken = testUtils.loginUser(mockUser);

        CreateUserGroupDto createGroupDto = new CreateUserGroupDto();
        createGroupDto.setGroupName("updated group name");

        UserGroup group = userGroupRepository.save(new UserGroup("test", registeredUser));

        MvcResult result = mockMvc.perform(put("/usergroups/" + group.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testUtils.asJsonString(createGroupDto)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        assert responseContent.contains("updated group name");

    }

    @Test
    public void testUpdateGroup_AuthenticatedUser_CannotUpdateOtherUserGroups() throws Exception {
        testUtils.signUpUser(mockUser);

        userRepository.save(mockUser2);

        UserGroup group = userGroupRepository.save(new UserGroup("test", mockUser2));

        String jwtToken = testUtils.loginUser(mockUser);

        CreateUserGroupDto updateGroupDto = new CreateUserGroupDto();
        updateGroupDto.setGroupName("updated group name");

        mockMvc.perform(put("/usergroups/" + group.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testUtils.asJsonString(updateGroupDto))).andExpect(status().isForbidden());

    }

    @Test
    public void testUpdateGroup_UnauthorizedUser_ForbidsUpdate() throws Exception {
        // Clear authentication (simulate no user logged in)
        SecurityContextHolder.clearContext();

        User registeredUser = testUtils.signUpUser(mockUser);

        UserGroup group = userGroupRepository.save(new UserGroup("test", registeredUser));

        CreateUserGroupDto updateGroupDto = new CreateUserGroupDto();
        updateGroupDto.setGroupName("updated group name");

        mockMvc.perform(put("/usergroups/" + group.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(testUtils.asJsonString(updateGroupDto))).andExpect(status().isForbidden());
    }

    // TESTS 'deleteGroup' endpoints
    @Test
    public void testDeleteGroup_AuthenticatedUser_CanDeleteOwnGroups() throws Exception {

        User registeredUser = testUtils.signUpUser(mockUser);
        String jwtToken = testUtils.loginUser(mockUser);

        UserGroup group = userGroupRepository.save(new UserGroup("test", registeredUser));

        mockMvc.perform(delete("/usergroups/" + group.getId())
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
        testUtils.signUpUser(mockUser);

        userRepository.save(mockUser2);

        UserGroup group = userGroupRepository.save(new UserGroup("test", mockUser2));

        String jwtToken = testUtils.loginUser(mockUser);

        mockMvc.perform(delete("/usergroups/" + group.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());
    }

}
