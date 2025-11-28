package com.militaryservices.app.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "user",schema = "ms")
public class User {

    @Id
    @Column(name = "username", columnDefinition = "VARCHAR(255)")
    private String username;

    @Column
    private String password;

    @Column
    private boolean enabled;

    @OneToOne
    @JoinColumn(name = "soldier_id")
    private Soldier soldier;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Authority> authorities;

    public User() {

    }

    public User(String username, String password, boolean enabled) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }

    public void setUserId(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setSoldier(Soldier soldier) {
        this.soldier = soldier;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    public String getUserId() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Soldier getSoldier() {
        return soldier;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }
}
