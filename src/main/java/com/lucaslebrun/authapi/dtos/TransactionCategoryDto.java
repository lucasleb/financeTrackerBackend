package com.lucaslebrun.authapi.dtos;

public class TransactionCategoryDto {

    private Integer id;

    private String name;

    private Integer userGroupId;

    public TransactionCategoryDto() {
    }

    public TransactionCategoryDto(String name, Integer userGroupId) {
        this.name = name;
        this.userGroupId = userGroupId;
    }

    public Integer getId() {
        return id;
    }

    public TransactionCategoryDto setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public TransactionCategoryDto setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getUserGroupId() {
        return userGroupId;
    }

    public TransactionCategoryDto setUserGroupId(Integer userGroupId) {
        this.userGroupId = userGroupId;
        return this;
    }
    
}
