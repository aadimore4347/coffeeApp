package com.coffee.coffeeApp.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public class CoffeeMachineDto {
    
    private Long id;
    
    @NotNull(message = "Facility ID is required")
    private Long facilityId;
    
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "ON|OFF", message = "Status must be either ON or OFF")
    private String status;
    
    @DecimalMin(value = "0.0", message = "Temperature must be positive")
    @DecimalMax(value = "200.0", message = "Temperature must be reasonable")
    private Float temperature;
    
    @DecimalMin(value = "0.0", message = "Water level must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Water level must be between 0 and 100")
    private Float waterLevel;
    
    @DecimalMin(value = "0.0", message = "Milk level must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Milk level must be between 0 and 100")
    private Float milkLevel;
    
    @DecimalMin(value = "0.0", message = "Beans level must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Beans level must be between 0 and 100")
    private Float beansLevel;
    
    private Boolean isActive;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdate;
    
    // Additional fields for API responses
    private String facilityName;
    private String facilityLocation;
    private Boolean isOperational;
    private Boolean hasLowSupplies;
    private Boolean hasLowWater;
    private Boolean hasLowMilk;
    private Boolean hasLowBeans;
    private Long totalUsageCount;
    private Long todayUsageCount;
    private Long activeAlertCount;
    private List<AlertLogDto> recentAlerts;
    
    // Constructors
    public CoffeeMachineDto() {}
    
    public CoffeeMachineDto(Long id, Long facilityId, String status) {
        this.id = id;
        this.facilityId = facilityId;
        this.status = status;
        this.isActive = true;
        this.waterLevel = 100.0f;
        this.milkLevel = 100.0f;
        this.beansLevel = 100.0f;
        this.temperature = 85.0f;
    }
    
    public CoffeeMachineDto(Long facilityId, String status) {
        this.facilityId = facilityId;
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
    
    public Long getFacilityId() {
        return facilityId;
    }
    
    public void setFacilityId(Long facilityId) {
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
    
    public String getFacilityName() {
        return facilityName;
    }
    
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }
    
    public String getFacilityLocation() {
        return facilityLocation;
    }
    
    public void setFacilityLocation(String facilityLocation) {
        this.facilityLocation = facilityLocation;
    }
    
    public Boolean getIsOperational() {
        return isOperational;
    }
    
    public void setIsOperational(Boolean isOperational) {
        this.isOperational = isOperational;
    }
    
    public Boolean getHasLowSupplies() {
        return hasLowSupplies;
    }
    
    public void setHasLowSupplies(Boolean hasLowSupplies) {
        this.hasLowSupplies = hasLowSupplies;
    }
    
    public Boolean getHasLowWater() {
        return hasLowWater;
    }
    
    public void setHasLowWater(Boolean hasLowWater) {
        this.hasLowWater = hasLowWater;
    }
    
    public Boolean getHasLowMilk() {
        return hasLowMilk;
    }
    
    public void setHasLowMilk(Boolean hasLowMilk) {
        this.hasLowMilk = hasLowMilk;
    }
    
    public Boolean getHasLowBeans() {
        return hasLowBeans;
    }
    
    public void setHasLowBeans(Boolean hasLowBeans) {
        this.hasLowBeans = hasLowBeans;
    }
    
    public Long getTotalUsageCount() {
        return totalUsageCount;
    }
    
    public void setTotalUsageCount(Long totalUsageCount) {
        this.totalUsageCount = totalUsageCount;
    }
    
    public Long getTodayUsageCount() {
        return todayUsageCount;
    }
    
    public void setTodayUsageCount(Long todayUsageCount) {
        this.todayUsageCount = todayUsageCount;
    }
    
    public Long getActiveAlertCount() {
        return activeAlertCount;
    }
    
    public void setActiveAlertCount(Long activeAlertCount) {
        this.activeAlertCount = activeAlertCount;
    }
    
    public List<AlertLogDto> getRecentAlerts() {
        return recentAlerts;
    }
    
    public void setRecentAlerts(List<AlertLogDto> recentAlerts) {
        this.recentAlerts = recentAlerts;
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
        return "CoffeeMachineDto{" +
                "id=" + id +
                ", facilityId=" + facilityId +
                ", status='" + status + '\'' +
                ", temperature=" + temperature +
                ", waterLevel=" + waterLevel +
                ", milkLevel=" + milkLevel +
                ", beansLevel=" + beansLevel +
                ", isActive=" + isActive +
                ", isOperational=" + isOperational() +
                '}';
    }
}