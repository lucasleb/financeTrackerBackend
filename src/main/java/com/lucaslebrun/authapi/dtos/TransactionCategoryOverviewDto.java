package com.lucaslebrun.authapi.dtos;

import java.math.BigDecimal;

import com.lucaslebrun.authapi.entities.TransactionCategory.TransactionType;

public class TransactionCategoryOverviewDto {

    private Integer id;

    private String name;

    private Integer userGroupId;

    private TransactionType type;

    private BigDecimal budget;

    private BigDecimal tracked;

    private BigDecimal completion;

    private BigDecimal remaining;

    private BigDecimal excess;

    public TransactionCategoryOverviewDto() {
    }

    public TransactionCategoryOverviewDto(String name, Integer userGroupId, TransactionType type, BigDecimal budget,
            BigDecimal totalTransactions, BigDecimal completion, BigDecimal remaining, BigDecimal excess) {
        this.name = name;
        this.userGroupId = userGroupId;
        this.type = type;
        this.budget = budget;
        this.tracked = totalTransactions;
        this.completion = completion;
        this.remaining = remaining;
        this.excess = excess;
    }

    public Integer getId() {
        return id;
    }

    public TransactionCategoryOverviewDto setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public TransactionCategoryOverviewDto setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getUserGroupId() {
        return userGroupId;
    }

    public TransactionCategoryOverviewDto setUserGroupId(Integer userGroupId) {
        this.userGroupId = userGroupId;
        return this;
    }

    public TransactionType getType() {
        return type;
    }

    public TransactionCategoryOverviewDto setType(TransactionType type) {
        this.type = type;
        return this;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public BigDecimal getTracked() {
        return tracked;
    }

    public TransactionCategoryOverviewDto setTracked(BigDecimal tracked) {
        this.tracked = tracked;
        return this;
    }

    public BigDecimal getCompletion() {
        return completion;
    }

    public TransactionCategoryOverviewDto setCompletion(BigDecimal completion) {
        this.completion = completion;
        return this;
    }

    public BigDecimal getRemaining() {
        return remaining;
    }

    public TransactionCategoryOverviewDto setRemaining(BigDecimal remaining) {
        this.remaining = remaining;
        return this;
    }

    public BigDecimal getExcess() {
        return excess;
    }

    public TransactionCategoryOverviewDto setExcess(BigDecimal excess) {
        this.excess = excess;
        return this;
    }

    public TransactionCategoryOverviewDto setBudget(BigDecimal budget) {
        this.budget = budget;
        return this;
    }

}
