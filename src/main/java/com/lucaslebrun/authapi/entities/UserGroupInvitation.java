package com.lucaslebrun.authapi.entities;

import jakarta.persistence.*;

@Entity
public class UserGroupInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "destinator_id", nullable = false)
    private User destinator;

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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getDestinator() {
        return destinator;
    }

    public void setDestinator(User destinator) {
        this.destinator = destinator;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    // constructors

    public UserGroupInvitation() {
    }

    public UserGroupInvitation(User author, User destinator, UserGroup userGroup) {
        this.author = author;
        this.destinator = destinator;
        this.userGroup = userGroup;
    }

}