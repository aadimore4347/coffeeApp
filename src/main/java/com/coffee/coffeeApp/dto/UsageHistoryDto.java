package com.coffee.coffeeApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public class UsageHistoryDto {
    
    private Long id;
    
    @NotNull(message = "Machine ID is required")
    private Long machineId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;
    
    @NotBlank(message = "Brew type is required")
    @Pattern(regexp = "ESPRESSO|AMERICANO|LATTE|CAPPUCCINO|MACCHIATO|MOCHA|BLACK_COFFEE|CUSTOM", 
             message = "Invalid brew type")
    private String brewType;
    
    private Boolean isActive;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdate;
    
    // Additional fields for API responses
    private Long machineFacilityId;
    private String machineFacilityName;
    private String machineFacilityLocation;
    private String userName;
    private String userRole;
    
    // Brewing parameters
    private Float sizeMultiplier;
    private Float strengthMultiplier;
    private Float milkRatio;
    private Float brewTemperature;
    private String specialInstructions;
    
    // Constructors
    public UsageHistoryDto() {}
    
    public UsageHistoryDto(Long id, Long machineId, Long userId, String brewType) {
        this.id = id;
        this.machineId = machineId;
        this.userId = userId;
        this.brewType = brewType;
        this.timestamp = LocalDateTime.now();
        this.isActive = true;
    }
    
    public UsageHistoryDto(Long machineId, Long userId, String brewType) {
        this.machineId = machineId;
        this.userId = userId;
        this.brewType = brewType;
        this.timestamp = LocalDateTime.now();
        this.isActive = true;
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
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getBrewType() {
        return brewType;
    }
    
    public void setBrewType(String brewType) {
        this.brewType = brewType;
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
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserRole() {
        return userRole;
    }
    
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
    
    public Float getSizeMultiplier() {
        return sizeMultiplier;
    }
    
    public void setSizeMultiplier(Float sizeMultiplier) {
        this.sizeMultiplier = sizeMultiplier;
    }
    
    public Float getStrengthMultiplier() {
        return strengthMultiplier;
    }
    
    public void setStrengthMultiplier(Float strengthMultiplier) {
        this.strengthMultiplier = strengthMultiplier;
    }
    
    public Float getMilkRatio() {
        return milkRatio;
    }
    
    public void setMilkRatio(Float milkRatio) {
        this.milkRatio = milkRatio;
    }
    
    public Float getBrewTemperature() {
        return brewTemperature;
    }
    
    public void setBrewTemperature(Float brewTemperature) {
        this.brewTemperature = brewTemperature;
    }
    
    public String getSpecialInstructions() {
        return specialInstructions;
    }
    
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
    
    // Business logic methods
    public boolean isActive() {
        return this.isActive != null && this.isActive;
    }
    
    public boolean isToday() {
        return timestamp != null && 
               timestamp.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }
    
    public boolean isRecent(int hours) {
        return timestamp != null && 
               timestamp.isAfter(LocalDateTime.now().minusHours(hours));
    }
    
    public boolean requiresMilk() {
        return "LATTE".equals(brewType) || "CAPPUCCINO".equals(brewType) || 
               "MACCHIATO".equals(brewType) || "MOCHA".equals(brewType) ||
               (milkRatio != null && milkRatio > 0.0f);
    }
    
    @Override
    public String toString() {
        return "UsageHistoryDto{" +
                "id=" + id +
                ", machineId=" + machineId +
                ", userId=" + userId +
                ", brewType='" + brewType + '\'' +
                ", timestamp=" + timestamp +
                ", isActive=" + isActive +
                '}';
    }
}