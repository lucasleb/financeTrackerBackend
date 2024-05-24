package com.lucaslebrun.authapi.dtos;

import java.math.BigDecimal;

import com.lucaslebrun.authapi.entities.TransactionCategory.TransactionType;

public class TransactionCategoryDto {

    private Integer id;

    private String name;

    private Integer userGroupId;

    private TransactionType type;

    private BigDecimal budget;

    public TransactionCategoryDto() {
    }

    public TransactionCategoryDto(String name, Integer userGroupId, TransactionType type, BigDecimal budget) {
        this.name = name;
        this.userGroupId = userGroupId;
        this.type = type;
        this.budget = budget;
    }

    public TransactionCategoryDto(String name, Integer userGroupId, TransactionType type) {
        this.name = name;
        this.userGroupId = userGroupId;
        this.type = type;
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

    public TransactionType getType() {
        return type;
    }

    public TransactionCategoryDto setType(TransactionType type) {
        this.type = type;
        return this;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public TransactionCategoryDto setBudget(BigDecimal budget) {
        this.budget = budget;
        return this;
    }

}
