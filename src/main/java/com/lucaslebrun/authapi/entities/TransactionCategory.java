package com.lucaslebrun.authapi.entities;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
public class TransactionCategory {

    public enum TransactionType {
        EXPENSE, INCOME
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_group_id", nullable = false)
    private UserGroup userGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column
    private BigDecimal budget;

    // getters and setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    // constructors

    public TransactionCategory() {
        // Default constructor
    }

    public TransactionCategory(String name, UserGroup userGroup, TransactionType type, BigDecimal budget) {
        this.name = name;
        this.userGroup = userGroup;
        this.type = type;
        this.budget = budget;
    }

    public TransactionCategory(String name, UserGroup userGroup, TransactionType type) {
        this.name = name;
        this.userGroup = userGroup;
        this.type = type;
    }

}
