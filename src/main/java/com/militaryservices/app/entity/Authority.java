package com.militaryservices.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "authority",schema = "ms")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id")
    private Long authId;

    @OneToOne
    @JoinColumn(name = "username")
    private User user;

    @Column
    private String authority;

    public Authority() {

    }

    public Authority(User user, String authority) {
        this.user = user;
        this.authority = authority;
    }

    public Long getAuthId() {
        return authId;
    }

    public User getUser() {
        return user;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
