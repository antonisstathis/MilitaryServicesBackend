package com.militaryservices.app.dto;

import com.militaryservices.app.entity.Authority;
import com.militaryservices.app.entity.Soldier;

import java.util.List;

public class UserDto {

    private String username;
    private String password;
    private boolean enabled;
    private int soldierId;
    private List<Authority> authorities;

    public UserDto(String username, boolean enabled, List<Authority> authorities) {
        this.username = username;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public UserDto(String username, String password, int soldierId, boolean enabled, List<Authority> authorities) {
        this.username = username;
        this.password = password;
        this.soldierId = soldierId;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public UserDto(String username, String password, boolean enabled, List<Authority> authorities) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getSoldierId() {
        return soldierId;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setSoldier(int soldierId) {
        this.soldierId = soldierId;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }
}
