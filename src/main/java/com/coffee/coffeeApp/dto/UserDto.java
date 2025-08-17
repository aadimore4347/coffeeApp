package com.coffee.coffeeApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public class UserDto {
    
    private String id;
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "FACILITY|ADMIN", message = "Role must be either FACILITY or ADMIN")
    private String role;
    
    private Boolean isActive;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdate;
    
    // Constructors
    public UserDto() {}
    
    public UserDto(String id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.isActive = true;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
    
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
    
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
    @Override
    public String toString() {
        return "UserDto{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", isActive=" + isActive +
                ", creationDate=" + creationDate +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}