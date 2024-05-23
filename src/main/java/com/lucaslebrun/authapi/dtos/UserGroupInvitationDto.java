package com.lucaslebrun.authapi.dtos;

public class UserGroupInvitationDto {

    private Integer invitationId;

    private Integer groupId;

    private String groupName;

    private String invitedUserEmail;

    private String authorEmail;


    public UserGroupInvitationDto() {
    }

    public UserGroupInvitationDto(Integer groupId, String invitedUserEmail) {
        this.groupId = groupId;
        this.invitedUserEmail = invitedUserEmail;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public UserGroupInvitationDto setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
        return this;
    }

    public UserGroupInvitationDto setGroupId(Integer groupId) {
        this.groupId = groupId;
        return this;
    }

    public String getInvitedUserEmail() {
        return invitedUserEmail;
    }

    public UserGroupInvitationDto setInvitedUserEmail(String invitedUserEmail) {
        this.invitedUserEmail = invitedUserEmail;
        return this;
    }

    public String getGroupName() {
        return groupName;
    }

    public UserGroupInvitationDto setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public Integer getInvitationId() {
        return invitationId;
    }

    public UserGroupInvitationDto setInvitationId(Integer invitationId) {
        this.invitationId = invitationId;
        return this;
    }

}
