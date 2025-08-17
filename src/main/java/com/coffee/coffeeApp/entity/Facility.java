package com.coffee.coffeeApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "facilities")
public class Facility {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @NotBlank(message = "Facility name is required")
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @NotBlank(message = "Location is required")
    @Column(name = "location", nullable = false)
    private String location;
    
    @NotNull(message = "Active status is required")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;
    
    @UpdateTimestamp
    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;
    
    // One-to-Many relationship with CoffeeMachine
    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CoffeeMachine> coffeeMachines;
    
    // Constructors
    public Facility() {}
    
    public Facility(String name, String location) {
        this.name = name;
        this.location = location;
        this.isActive = true;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
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
    
    public List<CoffeeMachine> getCoffeeMachines() {
        return coffeeMachines;
    }
    
    public void setCoffeeMachines(List<CoffeeMachine> coffeeMachines) {
        this.coffeeMachines = coffeeMachines;
    }
    
    // Business logic methods
    public boolean isActive() {
        return this.isActive != null && this.isActive;
    }
    
    public int getMachineCount() {
        return coffeeMachines != null ? coffeeMachines.size() : 0;
    }
    
    public long getActiveMachineCount() {
        return coffeeMachines != null ? 
            coffeeMachines.stream().filter(CoffeeMachine::isActive).count() : 0;
    }
    
    @Override
    public String toString() {
        return "Facility{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", isActive=" + isActive +
                ", creationDate=" + creationDate +
                '}';
    }
}