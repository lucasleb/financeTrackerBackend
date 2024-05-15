package com.lucaslebrun.authapi.dtos;

import java.util.List;

public class UserGroupDto {
    private Integer id;
    private String groupName;
    private List<UserDto> members;
    private UserDto admin;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<UserDto> getMembers() {
        return members;
    }

    public void setMembers(List<UserDto> members) {
        this.members = members;
    }

    public UserDto getAdmin() {
        return admin;
    }

    public void setAdmin(UserDto admin) {
        this.admin = admin;
    }

}
