package com.lucaslebrun.authapi.entities;

import jakarta.persistence.*;

@Entity
public class TransactionCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_group_id", nullable = false)
    private UserGroup userGroup;

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

    // constructors

    public TransactionCategory() {
        // Default constructor
    }

    public TransactionCategory(String name, UserGroup userGroup) {
        this.name = name;
        this.userGroup = userGroup;
    }

    // toString

    @Override
    public String toString() {
        return "TransactionCategories [id=" + id + ", name=" + name + ", userGroup=" + userGroup + "]";
    }

    
}
