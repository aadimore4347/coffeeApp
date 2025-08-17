package com.coffee.coffeeApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public class AlertLogDto {
    
    private Long id;
    
    @NotNull(message = "Machine ID is required")
    private Long machineId;
    
    @NotBlank(message = "Alert type is required")
    @Pattern(regexp = "LOW_WATER|LOW_MILK|LOW_BEANS|MALFUNCTION|OFFLINE|EMERGENCY|MAINTENANCE|TEMPERATURE", 
             message = "Invalid alert type")
    private String alertType;
    
    @NotBlank(message = "Message is required")
    private String message;
    
    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;
    
    private Boolean isActive;
    private Boolean isResolved;
    private LocalDateTime resolvedAt;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdate;
    
    // Additional fields for API responses
    private Long machineFacilityId;
    private String machineFacilityName;
    private String machineFacilityLocation;
    private String machineStatus;
    private String severity; // CRITICAL, HIGH, MEDIUM, LOW
    private String category; // SUPPLY, TECHNICAL, MAINTENANCE
    private Boolean requiresImmediateAttention;
    
    // Constructors
    public AlertLogDto() {}
    
    public AlertLogDto(Long id, Long machineId, String alertType, String message) {
        this.id = id;
        this.machineId = machineId;
        this.alertType = alertType;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.isActive = true;
        this.isResolved = false;
        this.setSeverityBasedOnType(alertType);
        this.setCategoryBasedOnType(alertType);
    }
    
    public AlertLogDto(Long machineId, String alertType, String message) {
        this.machineId = machineId;
        this.alertType = alertType;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.isActive = true;
        this.isResolved = false;
        this.setSeverityBasedOnType(alertType);
        this.setCategoryBasedOnType(alertType);
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getMachineId() {
        return machineId;
    }
    
    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }
    
    public String getAlertType() {
        return alertType;
    }
    
    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getIsResolved() {
        return isResolved;
    }
    
    public void setIsResolved(Boolean isResolved) {
        this.isResolved = isResolved;
        if (isResolved != null && isResolved && resolvedAt == null) {
            this.resolvedAt = LocalDateTime.now();
        }
    }
    
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    
    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
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
    
    public Long getMachineFacilityId() {
        return machineFacilityId;
    }
    
    public void setMachineFacilityId(Long machineFacilityId) {
        this.machineFacilityId = machineFacilityId;
    }
    
    public String getMachineFacilityName() {
        return machineFacilityName;
    }
    
    public void setMachineFacilityName(String machineFacilityName) {
        this.machineFacilityName = machineFacilityName;
    }
    
    public String getMachineFacilityLocation() {
        return machineFacilityLocation;
    }
    
    public void setMachineFacilityLocation(String machineFacilityLocation) {
        this.machineFacilityLocation = machineFacilityLocation;
    }
    
    public String getMachineStatus() {
        return machineStatus;
    }
    
    public void setMachineStatus(String machineStatus) {
        this.machineStatus = machineStatus;
    }
    
    public String getSeverity() {
        return severity;
    }
    
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Boolean getRequiresImmediateAttention() {
        return requiresImmediateAttention;
    }
    
    public void setRequiresImmediateAttention(Boolean requiresImmediateAttention) {
        this.requiresImmediateAttention = requiresImmediateAttention;
    }
    
    // Business logic methods
    public boolean isActive() {
        return this.isActive != null && this.isActive;
    }
    
    public boolean isResolved() {
        return this.isResolved != null && this.isResolved;
    }
    
    public boolean isCritical() {
        return "CRITICAL".equals(this.severity) || 
               "MALFUNCTION".equals(this.alertType) ||
               "OFFLINE".equals(this.alertType);
    }
    
    public boolean isSupplyAlert() {
        return "LOW_WATER".equals(this.alertType) ||
               "LOW_MILK".equals(this.alertType) ||
               "LOW_BEANS".equals(this.alertType);
    }
    
    public boolean requiresImmediateAttention() {
        return isCritical() && !isResolved();
    }
    
    public boolean isToday() {
        return timestamp != null && 
               timestamp.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }
    
    public boolean isRecent(int hours) {
        return timestamp != null && 
               timestamp.isAfter(LocalDateTime.now().minusHours(hours));
    }
    
    public void resolve() {
        this.isResolved = true;
        this.resolvedAt = LocalDateTime.now();
    }
    
    private void setSeverityBasedOnType(String alertType) {
        switch (alertType) {
            case "MALFUNCTION":
            case "OFFLINE":
            case "EMERGENCY":
                this.severity = "CRITICAL";
                break;
            case "LOW_WATER":
            case "LOW_MILK":
            case "LOW_BEANS":
                this.severity = "HIGH";
                break;
            case "MAINTENANCE":
                this.severity = "MEDIUM";
                break;
            case "TEMPERATURE":
                this.severity = "LOW";
                break;
            default:
                this.severity = "MEDIUM";
        }
    }
    
    private void setCategoryBasedOnType(String alertType) {
        switch (alertType) {
            case "LOW_WATER":
            case "LOW_MILK":
            case "LOW_BEANS":
                this.category = "SUPPLY";
                break;
            case "MALFUNCTION":
            case "OFFLINE":
            case "TEMPERATURE":
                this.category = "TECHNICAL";
                break;
            case "MAINTENANCE":
                this.category = "MAINTENANCE";
                break;
            default:
                this.category = "TECHNICAL";
        }
    }
    
    @Override
    public String toString() {
        return "AlertLogDto{" +
                "id=" + id +
                ", machineId=" + machineId +
                ", alertType='" + alertType + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", severity='" + severity + '\'' +
                ", isResolved=" + isResolved +
                '}';
    }
}