package com.lucaslebrun.authapi.entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_groups")
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private String groupName;

    @ManyToMany
    @JoinTable(name = "user_group_members", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> members = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    public UserGroup() {
    }

    public UserGroup(String groupName, User admin) {
        this.groupName = groupName;
        this.admin = admin;
        this.members.add(admin);
    }

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

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }
}
