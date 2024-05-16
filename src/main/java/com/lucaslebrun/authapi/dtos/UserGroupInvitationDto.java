package com.lucaslebrun.authapi.dtos;

import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.entities.UserGroup;


public class UserGroupInvitationDto {

    private User author;

    private UserGroup group;

    private User invitedUser;

    public UserGroup getGroup() {
        return group;
    }

    public UserGroupInvitationDto setGroup(UserGroup group) {
        this.group = group;
        return this;
    }

    public User getInvitedUser() {
        return invitedUser;
    }

    public UserGroupInvitationDto setInvitedUser(User invitedUser) {
        this.invitedUser = invitedUser;
        return this;
    }

    public User getAuthor() {
        return author;
    }

    public UserGroupInvitationDto setAuthor(User author) {
        this.author = author;
        return this;
    }
    
}
