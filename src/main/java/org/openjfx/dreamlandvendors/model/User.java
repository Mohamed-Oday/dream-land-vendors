package org.openjfx.dreamlandvendors.model;

import java.time.LocalDateTime;

public class User {
    private int userId;
    private String username;
    private String password;
    private String fullName;
    private UserRole role;
    private Integer vendorId;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    public enum UserRole {
        admin, vendor
    }

    // Default constructor
    public User() {
    }

    // Constructor with essential fields
    public User(String username, String password, String fullName, UserRole role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.isActive = true;
    }

    // Constructor with all fields
    public User(int userId, String username, String password, String fullName, UserRole role,
                Integer vendorId, boolean isActive, LocalDateTime createdAt, LocalDateTime lastLogin) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.vendorId = vendorId;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                ", vendorId=" + vendorId +
                ", isActive=" + isActive +
                '}';
    }
}
