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
@Table(name = "CoffeeMachine")
public class CoffeeMachine {
    
    @Id
    @Column(name = "id")
    private String id;
    
    @NotBlank(message = "Facility ID is required")
    @Column(name = "facilityId", nullable = false)
    private String facilityId;
    
    @NotBlank(message = "Status is required")
    @Column(name = "status", nullable = false)
    private String status; // ON/OFF
    
    @DecimalMin(value = "0.0", message = "Temperature must be positive")
    @DecimalMax(value = "200.0", message = "Temperature must be reasonable")
    @Column(name = "temperature")
    private Float temperature;
    
    @DecimalMin(value = "0.0", message = "Water level must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Water level must be between 0 and 100")
    @Column(name = "waterLevel")
    private Float waterLevel;
    
    @DecimalMin(value = "0.0", message = "Milk level must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Milk level must be between 0 and 100")
    @Column(name = "milkLevel")
    private Float milkLevel;
    
    @DecimalMin(value = "0.0", message = "Beans level must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Beans level must be between 0 and 100")
    @Column(name = "beansLevel")
    private Float beansLevel;
    
    @NotNull(message = "Active status is required")
    @Column(name = "isActive", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "creationDate", nullable = false, updatable = false)
    private LocalDateTime creationDate;
    
    @UpdateTimestamp
    @Column(name = "lastUpdate", nullable = false)
    private LocalDateTime lastUpdate;
    
    // Many-to-One relationship with Facility
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facilityId", referencedColumnName = "id", insertable = false, updatable = false)
    private Facility facility;
    
    // One-to-Many relationship with UsageHistory
    @OneToMany(mappedBy = "coffeeMachine", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UsageHistory> usageHistories;
    
    // One-to-Many relationship with AlertLog
    @OneToMany(mappedBy = "coffeeMachine", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<AlertLog> alertLogs;
    
    // Constructors
    public CoffeeMachine() {}
    
    public CoffeeMachine(String id, String facilityId, String status) {
        this.id = id;
        this.facilityId = facilityId;
        this.status = status;
        this.isActive = true;
        // Initialize levels to 100%
        this.waterLevel = 100.0f;
        this.milkLevel = 100.0f;
        this.beansLevel = 100.0f;
        this.temperature = 0.0f;
    }
    
    // Business Logic Methods
    public boolean isLowWaterLevel() {
        return waterLevel != null && waterLevel < 20.0f;
    }
    
    public boolean isLowMilkLevel() {
        return milkLevel != null && milkLevel < 20.0f;
    }
    
    public boolean isLowBeansLevel() {
        return beansLevel != null && beansLevel < 20.0f;
    }
    
    public boolean hasLowSupplies() {
        return isLowWaterLevel() || isLowMilkLevel() || isLowBeansLevel();
    }
    
    public boolean isOperational() {
        return "ON".equalsIgnoreCase(status) && isActive && !hasLowSupplies();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getFacilityId() {
        return facilityId;
    }
    
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
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
    
    public Facility getFacility() {
        return facility;
    }
    
    public void setFacility(Facility facility) {
        this.facility = facility;
    }
    
    public List<UsageHistory> getUsageHistories() {
        return usageHistories;
    }
    
    public void setUsageHistories(List<UsageHistory> usageHistories) {
        this.usageHistories = usageHistories;
    }
    
    public List<AlertLog> getAlertLogs() {
        return alertLogs;
    }
    
    public void setAlertLogs(List<AlertLog> alertLogs) {
        this.alertLogs = alertLogs;
    }
    
    @Override
    public String toString() {
        return "CoffeeMachine{" +
                "id='" + id + '\'' +
                ", facilityId='" + facilityId + '\'' +
                ", status='" + status + '\'' +
                ", temperature=" + temperature +
                ", waterLevel=" + waterLevel +
                ", milkLevel=" + milkLevel +
                ", beansLevel=" + beansLevel +
                ", isActive=" + isActive +
                ", creationDate=" + creationDate +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}