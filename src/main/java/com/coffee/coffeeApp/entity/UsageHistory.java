package com.coffee.coffeeApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "usage_history")
public class UsageHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    // Foreign key relationship with CoffeeMachine
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    @NotNull(message = "Coffee machine is required")
    private CoffeeMachine coffeeMachine;
    
    // Foreign key relationship with User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;
    
    @NotNull(message = "Timestamp is required")
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @NotBlank(message = "Brew type is required")
    @Column(name = "brew_type", nullable = false)
    private String brewType; // ESPRESSO, AMERICANO, LATTE, etc.
    
    @NotNull(message = "Active status is required")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;
    
    @UpdateTimestamp
    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;
    
    // Additional brewing parameters
    @Column(name = "size_multiplier")
    private Float sizeMultiplier = 1.0f;
    
    @Column(name = "strength_multiplier")
    private Float strengthMultiplier = 1.0f;
    
    @Column(name = "milk_ratio")
    private Float milkRatio = 0.0f;
    
    @Column(name = "brew_temperature")
    private Float brewTemperature;
    
    @Column(name = "special_instructions")
    private String specialInstructions;
    
    // Constructors
    public UsageHistory() {}
    
    public UsageHistory(CoffeeMachine coffeeMachine, User user, String brewType) {
        this.coffeeMachine = coffeeMachine;
        this.user = user;
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
    
    public CoffeeMachine getCoffeeMachine() {
        return coffeeMachine;
    }
    
    public void setCoffeeMachine(CoffeeMachine coffeeMachine) {
        this.coffeeMachine = coffeeMachine;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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
    
    @Override
    public String toString() {
        return "UsageHistory{" +
                "id=" + id +
                ", machineId=" + (coffeeMachine != null ? coffeeMachine.getId() : null) +
                ", userId=" + (user != null ? user.getId() : null) +
                ", brewType='" + brewType + '\'' +
                ", timestamp=" + timestamp +
                ", isActive=" + isActive +
                '}';
    }
}