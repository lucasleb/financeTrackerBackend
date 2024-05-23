package com.lucaslebrun.authapi.authapi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.lucaslebrun.authapi.dtos.UserGroupInvitationDto;
import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.entities.UserGroup;
import com.lucaslebrun.authapi.entities.UserGroupInvitation;
import com.lucaslebrun.authapi.repositories.UserGroupRepository;
import com.lucaslebrun.authapi.repositories.UserGroupInvitationRepository;
import com.lucaslebrun.authapi.repositories.UserRepository;

import com.lucaslebrun.authapi.services.AuthenticationService;

@SpringBootTest
@AutoConfigureMockMvc
public class UserGroupInvitationControllerTests {

        @Autowired
        private MockMvc mockMvc;

        @Mock
        private AuthenticationService authenticationService;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private UserGroupRepository userGroupRepository;

        @Autowired
        private UserGroupInvitationRepository userGroupInvitationRepository;

        private User mockUser;
        private User mockUser2;
        private User mockUser3;

        private TestUtils testUtils;

        @BeforeEach
        public void setup() {
                MockitoAnnotations.openMocks(this);
                userGroupInvitationRepository.deleteAll();
                userGroupRepository.deleteAll();
                userRepository.deleteAll();
                mockUser = new User("John Doe", "test@example.com", "password");
                mockUser2 = new User("John Doe2", "test2@example.com", "password2");
                mockUser3 = new User("John Doe3", "test3@example.com", "password3");

                testUtils = new TestUtils(mockMvc, userRepository);

        }

        // createUserGroupInvitation method:
        @Test
        public void testCreateUserGroupInvitationDto_GroupDoesNotExist() throws Exception {
                testUtils.signUpUser(mockUser);
                userRepository.save(mockUser2);

                String jwtToken = testUtils.loginUser(mockUser);

                UserGroupInvitationDto dto = new UserGroupInvitationDto(-1,
                                mockUser2.getEmail());

                mockMvc.perform(post("/invitations")
                                .header("Authorization", "Bearer " + jwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(testUtils.asJsonString(dto)))
                                .andExpect(status().is5xxServerError());

                // TODO: check if this is the correct status code. It should be 404 Not Found
        }

        @Test
        public void testCreateUserGroupInvitationDto_UserIsNotAdmin() throws Exception {
                User registeredUser = testUtils.signUpUser(mockUser);
                userRepository.save(mockUser2);
                userRepository.save(mockUser3);

                String jwtToken = testUtils.loginUser(mockUser);

                UserGroup group = new UserGroup("Group 1", mockUser3);
                group.getMembers().add(registeredUser);
                userGroupRepository.save(group);

                UserGroupInvitationDto dto = new UserGroupInvitationDto(group.getId(),
                                mockUser2.getEmail());

                mockMvc.perform(post("/invitations")
                                .header("Authorization", "Bearer " + jwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(testUtils.asJsonString(dto)))
                                .andExpect(status().isForbidden());
        }

        @Test
        public void testCreateUserGroupInvitationDto_UserDoesNotExist() throws Exception {
                User registeredUser = testUtils.signUpUser(mockUser);

                String jwtToken = testUtils.loginUser(mockUser);

                UserGroup group = userGroupRepository.save(new UserGroup("Group 1", registeredUser));

                UserGroupInvitationDto dto = new UserGroupInvitationDto(group.getId(),
                                mockUser2.getEmail());

                mockMvc.perform(post("/invitations")
                                .header("Authorization", "Bearer " + jwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(testUtils.asJsonString(dto)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void testCreateUserGroupInvitationDto_UserIsAlreadyMember() throws Exception {
                User registeredUser = testUtils.signUpUser(mockUser);
                userRepository.save(mockUser2);

                String jwtToken = testUtils.loginUser(mockUser);

                UserGroup group = userGroupRepository.save(new UserGroup("Group 1", registeredUser));
                group.getMembers().add(mockUser2);
                userGroupRepository.save(group);

                UserGroupInvitationDto dto = new UserGroupInvitationDto(group.getId(),
                                mockUser2.getEmail());

                mockMvc.perform(post("/invitations")
                                .header("Authorization", "Bearer " + jwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(testUtils.asJsonString(dto)))
                                .andExpect(status().isForbidden());
        }

        @Test
        public void testCreateUserGroupInvitationDto_InvitationAlreadyExists() throws Exception {
                User registeredUser = testUtils.signUpUser(mockUser);
                userRepository.save(mockUser2);

                String jwtToken = testUtils.loginUser(mockUser);

                UserGroup group = userGroupRepository.save(new UserGroup("Group 1", registeredUser));

                UserGroupInvitationDto dto = new UserGroupInvitationDto(group.getId(),
                                mockUser2.getEmail());

                mockMvc.perform(post("/invitations")
                                .header("Authorization", "Bearer " + jwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(testUtils.asJsonString(dto)))
                                .andExpect(status().isOk());

                mockMvc.perform(post("/invitations")
                                .header("Authorization", "Bearer " + jwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(testUtils.asJsonString(dto)))
                                .andExpect(status().isForbidden());
        }

        @Test
        public void testCreateUserGroupInvitationDto_Success() throws Exception {
                User registeredUser = testUtils.signUpUser(mockUser);
                userRepository.save(mockUser2);

                String jwtToken = testUtils.loginUser(mockUser);

                UserGroup group = userGroupRepository.save(new UserGroup("Group 1", registeredUser));

                UserGroupInvitationDto dto = new UserGroupInvitationDto(group.getId(),
                                mockUser2.getEmail());

                mockMvc.perform(post("/invitations")
                                .header("Authorization", "Bearer " + jwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(testUtils.asJsonString(dto)))
                                .andExpect(status().isOk());
        }

        // declineInvitation method:
        @Test
        public void testDeclineInvitation_InvitationDoesNotExist() throws Exception {
                testUtils.signUpUser(mockUser);
                userRepository.save(mockUser2);

                String jwtToken = testUtils.loginUser(mockUser);

                mockMvc.perform(delete("/invitations/-1/decline")
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().is5xxServerError());

        }

        @Test
        public void testDeclineInvitation_UserIsNotDestinator() throws Exception {
                User registeredUser = testUtils.signUpUser(mockUser);
                userRepository.save(mockUser2);

                String jwtToken = testUtils.loginUser(mockUser);

                UserGroup group = userGroupRepository.save(new UserGroup("Group 1", registeredUser));

                UserGroupInvitation invitation = userGroupInvitationRepository
                                .save(new UserGroupInvitation(registeredUser, mockUser2, group));

                mockMvc.perform(delete("/invitations/" + invitation.getId() + "/decline")
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isForbidden());
        }

        @Test
        public void testDeclineInvitation_Success() throws Exception {
                User registeredUser = testUtils.signUpUser(mockUser);
                userRepository.save(mockUser2);

                String jwtToken = testUtils.loginUser(mockUser);

                UserGroup group = userGroupRepository.save(new UserGroup("Group 1", mockUser2));

                UserGroupInvitation invitation = userGroupInvitationRepository
                                .save(new UserGroupInvitation(mockUser2, registeredUser, group));

                mockMvc.perform(delete("/invitations/" + invitation.getId() + "/decline")
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isNoContent());
        }

        // deleteInvitation method:
        @Test
        public void testDeleteInvitation_InvitationDoesNotExist() throws Exception {
                testUtils.signUpUser(mockUser);

                String jwtToken = testUtils.loginUser(mockUser);

                mockMvc.perform(delete("/invitations/-1")
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().is5xxServerError());
        }

        @Test
        public void testDeleteInvitation_UserIsNotAuthor() throws Exception {
                testUtils.signUpUser(mockUser);
                userRepository.save(mockUser2);
                userRepository.save(mockUser3);

                UserGroup group = userGroupRepository.save(new UserGroup("Group 1", mockUser2));

                UserGroupInvitation invitation = userGroupInvitationRepository
                                .save(new UserGroupInvitation(mockUser2, mockUser3, group));

                String jwtToken = testUtils.loginUser(mockUser);

                mockMvc.perform(delete("/invitations/" + invitation.getId())
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isForbidden());
        }

        @Test
        public void testDeleteInvitation_Success() throws Exception {
                User registeredUser = testUtils.signUpUser(mockUser);
                userRepository.save(mockUser2);

                UserGroup group = userGroupRepository.save(new UserGroup("Group 1", registeredUser));

                UserGroupInvitation invitation = userGroupInvitationRepository
                                .save(new UserGroupInvitation(registeredUser, mockUser2, group));

                String jwtToken = testUtils.loginUser(mockUser);

                mockMvc.perform(delete("/invitations/" + invitation.getId())
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isNoContent());
        }

        // acceptInvitation method:
        @Test
        public void testAcceptInvitation_InvitationDoesNotExist() throws Exception {
                testUtils.signUpUser(mockUser);

                String jwtToken = testUtils.loginUser(mockUser);

                mockMvc.perform(delete("/invitations/-1/accept")
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().is5xxServerError());
        }

        @Test
        public void testAcceptInvitation_UserIsNotDestinator() throws Exception {
                testUtils.signUpUser(mockUser);
                userRepository.save(mockUser2);
                userRepository.save(mockUser3);

                UserGroup group = userGroupRepository.save(new UserGroup("Group 1", mockUser2));

                UserGroupInvitation invitation = userGroupInvitationRepository
                                .save(new UserGroupInvitation(mockUser2, mockUser3, group));

                String jwtToken = testUtils.loginUser(mockUser);

                mockMvc.perform(delete("/invitations/" + invitation.getId() + "/accept")
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isForbidden());
        }

        @Test
        public void testAcceptInvitation_Success() throws Exception {
                User registeredUser = testUtils.signUpUser(mockUser);
                userRepository.save(mockUser2);

                UserGroup group = userGroupRepository.save(new UserGroup("Group 1", mockUser2));

                UserGroupInvitation invitation = userGroupInvitationRepository
                                .save(new UserGroupInvitation(mockUser2, registeredUser, group));

                String jwtToken = testUtils.loginUser(mockUser);

                mockMvc.perform(delete("/invitations/" + invitation.getId() + "/accept")
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isNoContent());

                // Check if the user is added to the group
                MvcResult result = mockMvc.perform(get("/usergroups")
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isOk())
                                .andReturn();

                assert result.getResponse().getContentAsString().contains("Group 1");

                // TODO: try and fix this method:
                // Assuming registeredUser is an instance of the User class
                // Set<UserGroup> userGroups = registeredUser.getGroups();

                // // Convert the Set of UserGroups to a Set of group names
                // Set<String> groupNames = userGroups.stream()
                // .map(UserGroup::getGroupName)
                // .collect(Collectors.toSet());

                // // Check if "Group 1" is in the set of group names
                // assertTrue(groupNames.contains("Group 1"));

        }

}
