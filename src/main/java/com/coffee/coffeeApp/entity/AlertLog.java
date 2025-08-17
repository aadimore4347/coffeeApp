package com.coffee.coffeeApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "alert_logs")
public class AlertLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    // Foreign key relationship with CoffeeMachine
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    @NotNull(message = "Coffee machine is required")
    private CoffeeMachine coffeeMachine;
    
    @NotBlank(message = "Alert type is required")
    @Column(name = "alert_type", nullable = false)
    private String alertType; // LOW_WATER, LOW_MILK, LOW_BEANS, MALFUNCTION, etc.
    
    @NotBlank(message = "Message is required")
    @Column(name = "message", nullable = false, length = 500)
    private String message;
    
    @NotNull(message = "Timestamp is required")
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @NotNull(message = "Active status is required")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "is_resolved", nullable = false)
    private Boolean isResolved = false;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @Column(name = "severity")
    private String severity; // CRITICAL, HIGH, MEDIUM, LOW
    
    @Column(name = "category")
    private String category; // SUPPLY, TECHNICAL, MAINTENANCE
    
    @CreationTimestamp
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;
    
    @UpdateTimestamp
    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;
    
    // Constructors
    public AlertLog() {}
    
    public AlertLog(CoffeeMachine coffeeMachine, String alertType, String message) {
        this.coffeeMachine = coffeeMachine;
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
    
    public CoffeeMachine getCoffeeMachine() {
        return coffeeMachine;
    }
    
    public void setCoffeeMachine(CoffeeMachine coffeeMachine) {
        this.coffeeMachine = coffeeMachine;
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
        if (isResolved && resolvedAt == null) {
            this.resolvedAt = LocalDateTime.now();
        }
    }
    
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    
    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
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
        return "AlertLog{" +
                "id=" + id +
                ", machineId=" + (coffeeMachine != null ? coffeeMachine.getId() : null) +
                ", alertType='" + alertType + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", severity='" + severity + '\'' +
                ", isResolved=" + isResolved +
                '}';
    }
}