package com.write.api.core.domain.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class UserModel {

    private Long id;
    private Long version;
    private String name;
    private String email;
    private String refreshToken;
    private String passwordHash;
    private boolean active;
    private boolean emailVerified;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime blockedAt;
    private int attemptsLoginFailed = 0;
    private Set<String> roles = new HashSet<>();

    public LocalDateTime getBlockedAt() {
        return blockedAt;
    }

    public void setBlockedAt(LocalDateTime blockedAt) {
        this.blockedAt = blockedAt;
    }

    public int getAttemptsLoginFailed() {
        return attemptsLoginFailed;
    }

    public void setAttemptsLoginFailed(int attemptsLoginFailed) {
        this.attemptsLoginFailed = attemptsLoginFailed;
    }

    public void sumAttemptLogin() {
        attemptsLoginFailed++;
    }

    public boolean addRole(String role) {
        boolean contains = this.roles.contains(role);

        if (contains)
            return true;

        return this.roles.add(role);
    }

    public boolean removeRole(String role) {
        boolean contains = this.roles.contains(role);

        if (!contains)
            return true;

        return this.roles.remove(role);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

}