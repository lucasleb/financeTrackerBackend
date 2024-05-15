package com.lucaslebrun.authapi.dtos;

import com.lucaslebrun.authapi.entities.User;

public class UserDto {

    private Integer id;
    private String fullName;
    private String email;

    public UserDto(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
