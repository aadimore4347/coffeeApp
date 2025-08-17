package com.coffee.coffeeApp.service;

import com.coffee.coffeeApp.dto.CoffeeMachineDto;
import com.coffee.coffeeApp.dto.BrewCommandDto;
import com.coffee.coffeeApp.entity.CoffeeMachine;
import com.coffee.coffeeApp.repository.CoffeeMachineRepository;
import com.coffee.coffeeApp.repository.FacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CoffeeMachineService {
    
    @Autowired
    private CoffeeMachineRepository coffeeMachineRepository;
    
    @Autowired
    private FacilityRepository facilityRepository;
    
    @Autowired
    private AlertLogService alertLogService;
    
    @Autowired
    private UsageHistoryService usageHistoryService;
    
    // Supply level thresholds
    private static final float LOW_SUPPLY_THRESHOLD = 20.0f;
    private static final float CRITICAL_SUPPLY_THRESHOLD = 10.0f;
    
    // Create new coffee machine
    public CoffeeMachineDto createCoffeeMachine(CoffeeMachineDto machineDto) {
        validateCoffeeMachineDto(machineDto);
        
        // Verify facility exists
        if (!facilityRepository.existsById(machineDto.getFacilityId())) {
            throw new IllegalArgumentException("Facility not found: " + machineDto.getFacilityId());
        }
        
        // Generate ID if not provided
        if (machineDto.getId() == null || machineDto.getId().isEmpty()) {
            machineDto.setId(UUID.randomUUID().toString());
        }
        
        CoffeeMachine machine = convertToEntity(machineDto);
        machine.setIsActive(true);
        
        // Initialize levels if not provided
        if (machine.getWaterLevel() == null) machine.setWaterLevel(100.0f);
        if (machine.getMilkLevel() == null) machine.setMilkLevel(100.0f);
        if (machine.getBeansLevel() == null) machine.setBeansLevel(100.0f);
        if (machine.getTemperature() == null) machine.setTemperature(0.0f);
        
        CoffeeMachine savedMachine = coffeeMachineRepository.save(machine);
        return convertToDtoWithStats(savedMachine);
    }
    
    // Get machine by ID
    @Transactional(readOnly = true)
    public Optional<CoffeeMachineDto> getMachineById(String id) {
        return coffeeMachineRepository.findById(id)
                .filter(CoffeeMachine::getIsActive)
                .map(this::convertToDtoWithStats);
    }
    
    // Get machines by facility
    @Transactional(readOnly = true)
    public List<CoffeeMachineDto> getMachinesByFacilityId(String facilityId) {
        return coffeeMachineRepository.findByFacilityIdAndIsActiveTrue(facilityId)
                .stream()
                .map(this::convertToDtoWithStats)
                .collect(Collectors.toList());
    }
    
    // Get all active machines
    @Transactional(readOnly = true)
    public List<CoffeeMachineDto> getAllMachines() {
        return coffeeMachineRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDtoWithStats)
                .collect(Collectors.toList());
    }
    
    // Get machines by status
    @Transactional(readOnly = true)
    public List<CoffeeMachineDto> getMachinesByStatus(String status) {
        validateStatus(status);
        return coffeeMachineRepository.findByStatusAndIsActiveTrue(status)
                .stream()
                .map(this::convertToDtoWithStats)
                .collect(Collectors.toList());
    }
    
    // Get operational machines
    @Transactional(readOnly = true)
    public List<CoffeeMachineDto> getOperationalMachines() {
        return coffeeMachineRepository.findOperationalMachines()
                .stream()
                .map(this::convertToDtoWithStats)
                .collect(Collectors.toList());
    }
    
    // Get machines with low supplies
    @Transactional(readOnly = true)
    public List<CoffeeMachineDto> getMachinesWithLowSupplies() {
        return coffeeMachineRepository.findMachinesWithLowSupplies()
                .stream()
                .map(this::convertToDtoWithStats)
                .collect(Collectors.toList());
    }
    
    // Get machines with critical supplies
    @Transactional(readOnly = true)
    public List<CoffeeMachineDto> getMachinesWithCriticalSupplies() {
        return coffeeMachineRepository.findMachinesWithCriticalSupplies()
                .stream()
                .map(this::convertToDtoWithStats)
                .collect(Collectors.toList());
    }
    
    // Get machines needing maintenance
    @Transactional(readOnly = true)
    public List<CoffeeMachineDto> getMachinesNeedingMaintenance() {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return coffeeMachineRepository.findMachinesNeedingMaintenance(since)
                .stream()
                .map(this::convertToDtoWithStats)
                .collect(Collectors.toList());
    }
    
    // Update machine levels (from MQTT or manual input)
    public CoffeeMachineDto updateMachineLevels(String machineId, Float waterLevel, 
                                              Float milkLevel, Float beansLevel, Float temperature) {
        CoffeeMachine machine = coffeeMachineRepository.findById(machineId)
                .filter(CoffeeMachine::getIsActive)
                .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + machineId));
        
        // Validate levels
        validateSupplyLevel(waterLevel, "Water level");
        validateSupplyLevel(milkLevel, "Milk level");
        validateSupplyLevel(beansLevel, "Beans level");
        validateTemperature(temperature);
        
        // Store previous levels for comparison
        Float previousWater = machine.getWaterLevel();
        Float previousMilk = machine.getMilkLevel();
        Float previousBeans = machine.getBeansLevel();
        
        // Update levels
        machine.setWaterLevel(waterLevel);
        machine.setMilkLevel(milkLevel);
        machine.setBeansLevel(beansLevel);
        machine.setTemperature(temperature);
        
        CoffeeMachine savedMachine = coffeeMachineRepository.save(machine);
        
        // Check for new low supply alerts
        checkAndCreateSupplyAlerts(savedMachine, previousWater, previousMilk, previousBeans);
        
        return convertToDtoWithStats(savedMachine);
    }
    
    // Update machine status
    public CoffeeMachineDto updateMachineStatus(String machineId, String status) {
        validateStatus(status);
        
        CoffeeMachine machine = coffeeMachineRepository.findById(machineId)
                .filter(CoffeeMachine::getIsActive)
                .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + machineId));
        
        String previousStatus = machine.getStatus();
        machine.setStatus(status);
        
        CoffeeMachine savedMachine = coffeeMachineRepository.save(machine);
        
        // Create alert if machine went offline
        if ("ON".equals(previousStatus) && "OFF".equals(status)) {
            alertLogService.createOfflineAlert(machineId, "Machine went offline");
        }
        
        return convertToDtoWithStats(savedMachine);
    }
    
    // Process brew command
    public BrewResult processBrew(BrewCommandDto brewCommand) {
        validateBrewCommand(brewCommand);
        
        CoffeeMachine machine = coffeeMachineRepository.findById(brewCommand.getMachineId())
                .filter(CoffeeMachine::getIsActive)
                .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + brewCommand.getMachineId()));
        
        // Check if machine is operational
        if (!machine.isOperational()) {
            return new BrewResult(false, "Machine is not operational. Please check supplies and status.");
        }
        
        // Calculate resource consumption based on brew type and customizations
        ResourceConsumption consumption = calculateResourceConsumption(brewCommand);
        
        // Check if machine has enough supplies
        if (machine.getWaterLevel() < consumption.waterUsage ||
            machine.getMilkLevel() < consumption.milkUsage ||
            machine.getBeansLevel() < consumption.beansUsage) {
            return new BrewResult(false, "Insufficient supplies for this brew.");
        }
        
        // Deduct supplies
        machine.setWaterLevel(machine.getWaterLevel() - consumption.waterUsage);
        machine.setMilkLevel(machine.getMilkLevel() - consumption.milkUsage);
        machine.setBeansLevel(machine.getBeansLevel() - consumption.beansUsage);
        
        coffeeMachineRepository.save(machine);
        
        // Create usage history record
        usageHistoryService.createUsageRecord(brewCommand.getMachineId(), 
                                            brewCommand.getBrewType(), 
                                            brewCommand.getUserId());
        
        // Check for low supply alerts after brewing
        checkAndCreateSupplyAlerts(machine, null, null, null);
        
        return new BrewResult(true, "Brew completed successfully!");
    }
    
    // Update machine
    public CoffeeMachineDto updateMachine(String id, CoffeeMachineDto machineDto) {
        CoffeeMachine existingMachine = coffeeMachineRepository.findById(id)
                .filter(CoffeeMachine::getIsActive)
                .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + id));
        
        validateCoffeeMachineDto(machineDto);
        
        // Update allowed fields
        existingMachine.setStatus(machineDto.getStatus());
        if (machineDto.getWaterLevel() != null) existingMachine.setWaterLevel(machineDto.getWaterLevel());
        if (machineDto.getMilkLevel() != null) existingMachine.setMilkLevel(machineDto.getMilkLevel());
        if (machineDto.getBeansLevel() != null) existingMachine.setBeansLevel(machineDto.getBeansLevel());
        if (machineDto.getTemperature() != null) existingMachine.setTemperature(machineDto.getTemperature());
        
        CoffeeMachine savedMachine = coffeeMachineRepository.save(existingMachine);
        return convertToDtoWithStats(savedMachine);
    }
    
    // Soft delete machine
    public void deleteMachine(String id) {
        CoffeeMachine machine = coffeeMachineRepository.findById(id)
                .filter(CoffeeMachine::getIsActive)
                .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + id));
        
        machine.setIsActive(false);
        machine.setStatus("OFF");
        coffeeMachineRepository.save(machine);
    }
    
    // Get machine statistics
    @Transactional(readOnly = true)
    public MachineStatistics getMachineStatistics() {
        List<CoffeeMachine> allMachines = coffeeMachineRepository.findByIsActiveTrue();
        long totalMachines = allMachines.size();
        long onlineMachines = coffeeMachineRepository.findByStatusAndIsActiveTrue("ON").size();
        long operationalMachines = coffeeMachineRepository.findOperationalMachines().size();
        long lowSupplyMachines = coffeeMachineRepository.findMachinesWithLowSupplies().size();
        long criticalSupplyMachines = coffeeMachineRepository.findMachinesWithCriticalSupplies().size();
        
        return new MachineStatistics(totalMachines, onlineMachines, operationalMachines, 
                                   lowSupplyMachines, criticalSupplyMachines);
    }
    
    // Helper methods
    private void checkAndCreateSupplyAlerts(CoffeeMachine machine, Float previousWater, 
                                          Float previousMilk, Float previousBeans) {
        // Check water level
        if (machine.getWaterLevel() < LOW_SUPPLY_THRESHOLD && 
            (previousWater == null || previousWater >= LOW_SUPPLY_THRESHOLD)) {
            alertLogService.createLowWaterAlert(machine.getId(), machine.getWaterLevel());
        }
        
        // Check milk level
        if (machine.getMilkLevel() < LOW_SUPPLY_THRESHOLD && 
            (previousMilk == null || previousMilk >= LOW_SUPPLY_THRESHOLD)) {
            alertLogService.createLowMilkAlert(machine.getId(), machine.getMilkLevel());
        }
        
        // Check beans level
        if (machine.getBeansLevel() < LOW_SUPPLY_THRESHOLD && 
            (previousBeans == null || previousBeans >= LOW_SUPPLY_THRESHOLD)) {
            alertLogService.createLowBeansAlert(machine.getId(), machine.getBeansLevel());
        }
    }
    
    private ResourceConsumption calculateResourceConsumption(BrewCommandDto brewCommand) {
        // Base consumption per brew type
        float baseWater = 50.0f;  // ml
        float baseMilk = 0.0f;    // ml
        float baseBeans = 10.0f;  // grams
        
        // Adjust based on brew type
        switch (brewCommand.getBrewType().toUpperCase()) {
            case "ESPRESSO":
                baseWater = 30.0f;
                baseBeans = 8.0f;
                break;
            case "AMERICANO":
                baseWater = 120.0f;
                baseBeans = 8.0f;
                break;
            case "LATTE":
                baseWater = 60.0f;
                baseMilk = 150.0f;
                baseBeans = 8.0f;
                break;
            case "CAPPUCCINO":
                baseWater = 60.0f;
                baseMilk = 100.0f;
                baseBeans = 8.0f;
                break;
            case "MACCHIATO":
                baseWater = 30.0f;
                baseMilk = 50.0f;
                baseBeans = 8.0f;
                break;
            case "MOCHA":
                baseWater = 60.0f;
                baseMilk = 120.0f;
                baseBeans = 10.0f;
                break;
        }
        
        // Apply size multiplier
        float sizeMultiplier = brewCommand.getSize() != null ? brewCommand.getSize() : 1.0f;
        baseWater *= sizeMultiplier;
        baseMilk *= sizeMultiplier;
        
        // Apply strength multiplier (affects beans only)
        float strengthMultiplier = brewCommand.getStrength() != null ? brewCommand.getStrength() : 1.0f;
        baseBeans *= strengthMultiplier;
        
        // Apply milk ratio
        if (brewCommand.getMilkRatio() != null && brewCommand.getMilkRatio() > 0) {
            baseMilk *= brewCommand.getMilkRatio();
        }
        
        // Convert to percentage consumption (assuming 1000ml tank capacity)
        float waterPercentage = (baseWater / 1000.0f) * 100.0f;
        float milkPercentage = (baseMilk / 1000.0f) * 100.0f;
        float beansPercentage = (baseBeans / 500.0f) * 100.0f; // 500g beans capacity
        
        return new ResourceConsumption(waterPercentage, milkPercentage, beansPercentage);
    }
    
    // Validation methods
    private void validateCoffeeMachineDto(CoffeeMachineDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Machine data cannot be null");
        }
        if (dto.getFacilityId() == null || dto.getFacilityId().trim().isEmpty()) {
            throw new IllegalArgumentException("Facility ID is required");
        }
        if (dto.getStatus() == null || dto.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("Status is required");
        }
        validateStatus(dto.getStatus());
    }
    
    private void validateStatus(String status) {
        if (!"ON".equals(status) && !"OFF".equals(status)) {
            throw new IllegalArgumentException("Status must be ON or OFF");
        }
    }
    
    private void validateSupplyLevel(Float level, String levelName) {
        if (level == null) {
            throw new IllegalArgumentException(levelName + " cannot be null");
        }
        if (level < 0.0f || level > 100.0f) {
            throw new IllegalArgumentException(levelName + " must be between 0 and 100");
        }
    }
    
    private void validateTemperature(Float temperature) {
        if (temperature == null) {
            throw new IllegalArgumentException("Temperature cannot be null");
        }
        if (temperature < 0.0f || temperature > 200.0f) {
            throw new IllegalArgumentException("Temperature must be between 0 and 200 degrees");
        }
    }
    
    private void validateBrewCommand(BrewCommandDto brewCommand) {
        if (brewCommand == null) {
            throw new IllegalArgumentException("Brew command cannot be null");
        }
        if (brewCommand.getMachineId() == null || brewCommand.getMachineId().trim().isEmpty()) {
            throw new IllegalArgumentException("Machine ID is required");
        }
        if (brewCommand.getBrewType() == null || brewCommand.getBrewType().trim().isEmpty()) {
            throw new IllegalArgumentException("Brew type is required");
        }
        if (brewCommand.getUserId() == null || brewCommand.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
    }
    
    // Conversion methods
    private CoffeeMachineDto convertToDtoWithStats(CoffeeMachine machine) {
        CoffeeMachineDto dto = new CoffeeMachineDto();
        dto.setId(machine.getId());
        dto.setFacilityId(machine.getFacilityId());
        dto.setStatus(machine.getStatus());
        dto.setTemperature(machine.getTemperature());
        dto.setWaterLevel(machine.getWaterLevel());
        dto.setMilkLevel(machine.getMilkLevel());
        dto.setBeansLevel(machine.getBeansLevel());
        dto.setIsActive(machine.getIsActive());
        dto.setCreationDate(machine.getCreationDate());
        dto.setLastUpdate(machine.getLastUpdate());
        
        // Add computed fields
        dto.setIsOperational(machine.isOperational());
        dto.setHasLowSupplies(machine.hasLowSupplies());
        dto.setHasLowWater(machine.isLowWaterLevel());
        dto.setHasLowMilk(machine.isLowMilkLevel());
        dto.setHasLowBeans(machine.isLowBeansLevel());
        
        return dto;
    }
    
    private CoffeeMachine convertToEntity(CoffeeMachineDto dto) {
        CoffeeMachine machine = new CoffeeMachine();
        machine.setId(dto.getId());
        machine.setFacilityId(dto.getFacilityId());
        machine.setStatus(dto.getStatus());
        machine.setTemperature(dto.getTemperature());
        machine.setWaterLevel(dto.getWaterLevel());
        machine.setMilkLevel(dto.getMilkLevel());
        machine.setBeansLevel(dto.getBeansLevel());
        machine.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return machine;
    }
    
    // Inner classes
    public static class MachineStatistics {
        private final long totalMachines;
        private final long onlineMachines;
        private final long operationalMachines;
        private final long lowSupplyMachines;
        private final long criticalSupplyMachines;
        
        public MachineStatistics(long totalMachines, long onlineMachines, long operationalMachines,
                               long lowSupplyMachines, long criticalSupplyMachines) {
            this.totalMachines = totalMachines;
            this.onlineMachines = onlineMachines;
            this.operationalMachines = operationalMachines;
            this.lowSupplyMachines = lowSupplyMachines;
            this.criticalSupplyMachines = criticalSupplyMachines;
        }
        
        // Getters
        public long getTotalMachines() { return totalMachines; }
        public long getOnlineMachines() { return onlineMachines; }
        public long getOperationalMachines() { return operationalMachines; }
        public long getLowSupplyMachines() { return lowSupplyMachines; }
        public long getCriticalSupplyMachines() { return criticalSupplyMachines; }
    }
    
    public static class BrewResult {
        private final boolean success;
        private final String message;
        
        public BrewResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    
    private static class ResourceConsumption {
        final float waterUsage;
        final float milkUsage;
        final float beansUsage;
        
        ResourceConsumption(float waterUsage, float milkUsage, float beansUsage) {
            this.waterUsage = waterUsage;
            this.milkUsage = milkUsage;
            this.beansUsage = beansUsage;
        }
    }
}