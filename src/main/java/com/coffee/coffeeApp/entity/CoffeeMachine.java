package com.coffee.coffeeApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "coffee_machines")
public class CoffeeMachine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    // Foreign key relationship with Facility
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    @NotNull(message = "Facility is required")
    private Facility facility;
    
    @NotBlank(message = "Status is required")
    @Column(name = "status", nullable = false)
    private String status; // ON/OFF
    
    @DecimalMin(value = "0.0", message = "Temperature must be positive")
    @DecimalMax(value = "200.0", message = "Temperature must be reasonable")
    @Column(name = "temperature")
    private Float temperature;
    
    @DecimalMin(value = "0.0", message = "Water level must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Water level must be between 0 and 100")
    @Column(name = "water_level")
    private Float waterLevel;
    
    @DecimalMin(value = "0.0", message = "Milk level must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Milk level must be between 0 and 100")
    @Column(name = "milk_level")
    private Float milkLevel;
    
    @DecimalMin(value = "0.0", message = "Beans level must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Beans level must be between 0 and 100")
    @Column(name = "beans_level")
    private Float beansLevel;
    
    @NotNull(message = "Active status is required")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;
    
    @UpdateTimestamp
    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;
    
    // One-to-Many relationships
    @OneToMany(mappedBy = "coffeeMachine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UsageHistory> usageHistory;
    
    @OneToMany(mappedBy = "coffeeMachine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AlertLog> alertLogs;
    
    // Constructors
    public CoffeeMachine() {}
    
    public CoffeeMachine(Facility facility, String status) {
        this.facility = facility;
        this.status = status;
        this.isActive = true;
        this.waterLevel = 100.0f;
        this.milkLevel = 100.0f;
        this.beansLevel = 100.0f;
        this.temperature = 85.0f;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Facility getFacility() {
        return facility;
    }
    
    public void setFacility(Facility facility) {
        this.facility = facility;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Float getTemperature() {
        return temperature;
    }
    
    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }
    
    public Float getWaterLevel() {
        return waterLevel;
    }
    
    public void setWaterLevel(Float waterLevel) {
        this.waterLevel = waterLevel;
    }
    
    public Float getMilkLevel() {
        return milkLevel;
    }
    
    public void setMilkLevel(Float milkLevel) {
        this.milkLevel = milkLevel;
    }
    
    public Float getBeansLevel() {
        return beansLevel;
    }
    
    public void setBeansLevel(Float beansLevel) {
        this.beansLevel = beansLevel;
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
    
    public List<UsageHistory> getUsageHistory() {
        return usageHistory;
    }
    
    public void setUsageHistory(List<UsageHistory> usageHistory) {
        this.usageHistory = usageHistory;
    }
    
    public List<AlertLog> getAlertLogs() {
        return alertLogs;
    }
    
    public void setAlertLogs(List<AlertLog> alertLogs) {
        this.alertLogs = alertLogs;
    }
    
    // Business logic methods
    public boolean isActive() {
        return this.isActive != null && this.isActive;
    }
    
    public boolean isOn() {
        return "ON".equals(this.status);
    }
    
    public boolean isOperational() {
        return isOn() && isActive() && 
               waterLevel != null && waterLevel > 10.0f &&
               milkLevel != null && milkLevel > 10.0f &&
               beansLevel != null && beansLevel > 10.0f;
    }
    
    public boolean hasLowSupplies() {
        return (waterLevel != null && waterLevel < 20.0f) ||
               (milkLevel != null && milkLevel < 20.0f) ||
               (beansLevel != null && beansLevel < 20.0f);
    }
    
    public boolean hasCriticalSupplies() {
        return (waterLevel != null && waterLevel < 10.0f) ||
               (milkLevel != null && milkLevel < 10.0f) ||
               (beansLevel != null && beansLevel < 10.0f);
    }
    
    public boolean needsMaintenance() {
        return hasCriticalSupplies() || !isOperational();
    }
    
    @Override
    public String toString() {
        return "CoffeeMachine{" +
                "id=" + id +
                ", facilityId=" + (facility != null ? facility.getId() : null) +
                ", status='" + status + '\'' +
                ", temperature=" + temperature +
                ", waterLevel=" + waterLevel +
                ", milkLevel=" + milkLevel +
                ", beansLevel=" + beansLevel +
                ", isActive=" + isActive +
                '}';
    }
}