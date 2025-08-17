package com.coffee.coffeeApp.service;

import com.coffee.coffeeApp.dto.FacilityDto;
import com.coffee.coffeeApp.entity.Facility;
import com.coffee.coffeeApp.repository.FacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FacilityService {
    
    @Autowired
    private FacilityRepository facilityRepository;
    
    // Create new facility
    public FacilityDto createFacility(FacilityDto facilityDto) {
        validateFacilityDto(facilityDto);
        
        // Check if facility name already exists
        if (facilityRepository.existsByNameAndIsActiveTrue(facilityDto.getName())) {
            throw new IllegalArgumentException("Facility name already exists: " + facilityDto.getName());
        }
        
        Facility facility = convertToEntity(facilityDto);
        facility.setIsActive(true);
        
        Facility savedFacility = facilityRepository.save(facility);
        return convertToDto(savedFacility);
    }
    
    // Get facility by ID
    @Transactional(readOnly = true)
    public Optional<FacilityDto> getFacilityById(Long id) {
        return facilityRepository.findById(id)
                .filter(Facility::isActive)
                .map(this::convertToDto);
    }
    
    // Get facility by name
    @Transactional(readOnly = true)
    public Optional<FacilityDto> getFacilityByName(String name) {
        return facilityRepository.findByNameAndIsActiveTrue(name)
                .map(this::convertToDto);
    }
    
    // Get all facilities
    @Transactional(readOnly = true)
    public List<FacilityDto> getAllFacilities() {
        return facilityRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get facilities by location
    @Transactional(readOnly = true)
    public List<FacilityDto> getFacilitiesByLocation(String location) {
        return facilityRepository.findByLocationAndIsActiveTrue(location)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Search facilities by location keyword
    @Transactional(readOnly = true)
    public List<FacilityDto> searchFacilitiesByLocation(String locationKeyword) {
        return facilityRepository.findByLocationContainingIgnoreCaseAndIsActiveTrue(locationKeyword)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get facilities with active machines
    @Transactional(readOnly = true)
    public List<FacilityDto> getFacilitiesWithActiveMachines() {
        return facilityRepository.findFacilitiesWithActiveMachines()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get facilities with low supply machines
    @Transactional(readOnly = true)
    public List<FacilityDto> getFacilitiesWithLowSupplyMachines() {
        return facilityRepository.findFacilitiesWithLowSupplyMachines()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get facility with machines details
    @Transactional(readOnly = true)
    public Optional<FacilityDto> getFacilityWithMachines(Long facilityId) {
        return facilityRepository.findById(facilityId)
                .filter(Facility::isActive)
                .map(this::convertToDtoWithMachines);
    }
    
    // Update facility
    public FacilityDto updateFacility(Long id, FacilityDto facilityDto) {
        Facility existingFacility = facilityRepository.findById(id)
                .filter(Facility::isActive)
                .orElseThrow(() -> new RuntimeException("Facility not found: " + id));
        
        validateFacilityDto(facilityDto);
        
        // Check if name already exists (excluding current facility)
        if (!existingFacility.getName().equals(facilityDto.getName()) &&
            facilityRepository.existsByNameAndIsActiveTrue(facilityDto.getName())) {
            throw new IllegalArgumentException("Facility name already exists: " + facilityDto.getName());
        }
        
        // Update fields
        existingFacility.setName(facilityDto.getName());
        existingFacility.setLocation(facilityDto.getLocation());
        
        Facility updatedFacility = facilityRepository.save(existingFacility);
        return convertToDto(updatedFacility);
    }
    
    // Delete facility (soft delete)
    public void deleteFacility(Long id) {
        Facility facility = facilityRepository.findById(id)
                .filter(Facility::isActive)
                .orElseThrow(() -> new RuntimeException("Facility not found: " + id));
        
        facility.setIsActive(false);
        facilityRepository.save(facility);
    }
    
    // Reactivate facility
    public FacilityDto reactivateFacility(Long id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found: " + id));
        
        facility.setIsActive(true);
        Facility reactivatedFacility = facilityRepository.save(facility);
        return convertToDto(reactivatedFacility);
    }
    
    // Check if facility exists
    @Transactional(readOnly = true)
    public boolean facilityExists(Long id) {
        return facilityRepository.existsById(id);
    }
    
    // Check if facility name exists
    @Transactional(readOnly = true)
    public boolean facilityNameExists(String name) {
        return facilityRepository.existsByNameAndIsActiveTrue(name);
    }
    
    // Get recently created facilities
    @Transactional(readOnly = true)
    public List<FacilityDto> getRecentlyCreatedFacilities(int hours) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hours);
        return facilityRepository.findRecentlyCreatedFacilities(cutoffTime)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get facilities by machine count range
    @Transactional(readOnly = true)
    public List<FacilityDto> getFacilitiesByMachineCountRange(Long minCount, Long maxCount) {
        return facilityRepository.findFacilitiesByMachineCountRange(minCount, maxCount)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Validation helper
    private void validateFacilityDto(FacilityDto facilityDto) {
        if (facilityDto == null) {
            throw new IllegalArgumentException("Facility data cannot be null");
        }
        if (facilityDto.getName() == null || facilityDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Facility name is required");
        }
        if (facilityDto.getLocation() == null || facilityDto.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Location is required");
        }
    }
    
    // Convert entity to DTO
    private FacilityDto convertToDto(Facility facility) {
        FacilityDto dto = new FacilityDto();
        dto.setId(facility.getId());
        dto.setName(facility.getName());
        dto.setLocation(facility.getLocation());
        dto.setIsActive(facility.getIsActive());
        dto.setCreationDate(facility.getCreationDate());
        dto.setLastUpdate(facility.getLastUpdate());
        
        // Add machine statistics
        if (facility.getCoffeeMachines() != null) {
            dto.setTotalMachines((long) facility.getCoffeeMachines().size());
            dto.setActiveMachines(facility.getCoffeeMachines().stream()
                    .filter(machine -> machine.isActive() && "ON".equals(machine.getStatus()))
                    .count());
            dto.setOperationalMachines(facility.getCoffeeMachines().stream()
                    .filter(machine -> machine.isOperational())
                    .count());
            dto.setMachinesWithLowSupplies(facility.getCoffeeMachines().stream()
                    .filter(machine -> machine.hasLowSupplies())
                    .count());
        }
        
        return dto;
    }
    
    // Convert entity to DTO with machines
    private FacilityDto convertToDtoWithMachines(Facility facility) {
        FacilityDto dto = convertToDto(facility);
        // Note: Machine conversion would be handled by CoffeeMachineService
        // to avoid circular dependencies
        return dto;
    }
    
    // Convert DTO to entity
    private Facility convertToEntity(FacilityDto dto) {
        Facility facility = new Facility();
        if (dto.getId() != null) {
            facility.setId(dto.getId());
        }
        facility.setName(dto.getName());
        facility.setLocation(dto.getLocation());
        facility.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return facility;
    }
}