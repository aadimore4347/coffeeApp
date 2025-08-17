package com.coffee.coffeeApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "AlertLog")
public class AlertLog {
    
    @Id
    @Column(name = "id")
    private String id;
    
    @NotBlank(message = "Machine ID is required")
    @Column(name = "machineId", nullable = false)
    private String machineId;
    
    @NotBlank(message = "Alert type is required")
    @Column(name = "alertType", nullable = false)
    private String alertType; // LOW_WATER, LOW_MILK, LOW_BEANS, MALFUNCTION, etc.
    
    @NotBlank(message = "Message is required")
    @Column(name = "message", nullable = false, length = 500)
    private String message;
    
    @NotNull(message = "Timestamp is required")
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @NotNull(message = "Active status is required")
    @Column(name = "isActive", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "creationDate", nullable = false, updatable = false)
    private LocalDateTime creationDate;
    
    @UpdateTimestamp
    @Column(name = "lastUpdate", nullable = false)
    private LocalDateTime lastUpdate;
    
    // Many-to-One relationship with CoffeeMachine
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machineId", referencedColumnName = "id", insertable = false, updatable = false)
    private CoffeeMachine coffeeMachine;
    
    // Constructors
    public AlertLog() {}
    
    public AlertLog(String id, String machineId, String alertType, String message) {
        this.id = id;
        this.machineId = machineId;
        this.alertType = alertType;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.isActive = true;
    }
    
    // Business Logic Methods
    public boolean isCriticalAlert() {
        return "MALFUNCTION".equalsIgnoreCase(alertType) || 
               "OFFLINE".equalsIgnoreCase(alertType) ||
               "EMERGENCY".equalsIgnoreCase(alertType);
    }
    
    public boolean isSupplyAlert() {
        return "LOW_WATER".equalsIgnoreCase(alertType) || 
               "LOW_MILK".equalsIgnoreCase(alertType) ||
               "LOW_BEANS".equalsIgnoreCase(alertType);
    }
    
    public boolean isRecentAlert(int hoursAgo) {
        return timestamp != null && timestamp.isAfter(LocalDateTime.now().minusHours(hoursAgo));
    }
    
    public static AlertLog createLowWaterAlert(String id, String machineId, float currentLevel) {
        String message = String.format("Water level is critically low: %.1f%%. Please refill immediately.", currentLevel);
        return new AlertLog(id, machineId, "LOW_WATER", message);
    }
    
    public static AlertLog createLowMilkAlert(String id, String machineId, float currentLevel) {
        String message = String.format("Milk level is critically low: %.1f%%. Please refill immediately.", currentLevel);
        return new AlertLog(id, machineId, "LOW_MILK", message);
    }
    
    public static AlertLog createLowBeansAlert(String id, String machineId, float currentLevel) {
        String message = String.format("Coffee beans level is critically low: %.1f%%. Please refill immediately.", currentLevel);
        return new AlertLog(id, machineId, "LOW_BEANS", message);
    }
    
    public static AlertLog createMalfunctionAlert(String id, String machineId, String issue) {
        String message = String.format("Machine malfunction detected: %s. Maintenance required.", issue);
        return new AlertLog(id, machineId, "MALFUNCTION", message);
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getMachineId() {
        return machineId;
    }
    
    public void setMachineId(String machineId) {
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
    
    public CoffeeMachine getCoffeeMachine() {
        return coffeeMachine;
    }
    
    public void setCoffeeMachine(CoffeeMachine coffeeMachine) {
        this.coffeeMachine = coffeeMachine;
    }
    
    @Override
    public String toString() {
        return "AlertLog{" +
                "id='" + id + '\'' +
                ", machineId='" + machineId + '\'' +
                ", alertType='" + alertType + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", isActive=" + isActive +
                ", creationDate=" + creationDate +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}