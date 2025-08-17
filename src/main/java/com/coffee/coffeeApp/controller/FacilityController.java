package com.coffee.coffeeApp.controller;

import com.coffee.coffeeApp.dto.FacilityDto;
import com.coffee.coffeeApp.service.FacilityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/facilities")
@CrossOrigin(origins = "*")
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

    // Create new facility
    @PostMapping
    public ResponseEntity<FacilityDto> createFacility(@Valid @RequestBody FacilityDto facilityDto) {
        try {
            FacilityDto createdFacility = facilityService.createFacility(facilityDto);
            return new ResponseEntity<>(createdFacility, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get facility by ID
    @GetMapping("/{id}")
    public ResponseEntity<FacilityDto> getFacilityById(@PathVariable String id) {
        try {
            Optional<FacilityDto> facility = facilityService.getFacilityById(id);
            return facility.map(facilityDto -> new ResponseEntity<>(facilityDto, HttpStatus.OK))
                          .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get facility by name
    @GetMapping("/name/{name}")
    public ResponseEntity<FacilityDto> getFacilityByName(@PathVariable String name) {
        try {
            Optional<FacilityDto> facility = facilityService.getFacilityByName(name);
            return facility.map(facilityDto -> new ResponseEntity<>(facilityDto, HttpStatus.OK))
                          .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all facilities
    @GetMapping
    public ResponseEntity<List<FacilityDto>> getAllFacilities() {
        try {
            List<FacilityDto> facilities = facilityService.getAllFacilities();
            return new ResponseEntity<>(facilities, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get facilities by location
    @GetMapping("/location/{location}")
    public ResponseEntity<List<FacilityDto>> getFacilitiesByLocation(@PathVariable String location) {
        try {
            List<FacilityDto> facilities = facilityService.getFacilitiesByLocation(location);
            return new ResponseEntity<>(facilities, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Search facilities by location keyword
    @GetMapping("/search/location")
    public ResponseEntity<List<FacilityDto>> searchFacilitiesByLocation(@RequestParam String keyword) {
        try {
            List<FacilityDto> facilities = facilityService.searchFacilitiesByLocation(keyword);
            return new ResponseEntity<>(facilities, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get facilities with active machines
    @GetMapping("/with-active-machines")
    public ResponseEntity<List<FacilityDto>> getFacilitiesWithActiveMachines() {
        try {
            List<FacilityDto> facilities = facilityService.getFacilitiesWithActiveMachines();
            return new ResponseEntity<>(facilities, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get facilities with low supply machines
    @GetMapping("/with-low-supply-machines")
    public ResponseEntity<List<FacilityDto>> getFacilitiesWithLowSupplyMachines() {
        try {
            List<FacilityDto> facilities = facilityService.getFacilitiesWithLowSupplyMachines();
            return new ResponseEntity<>(facilities, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get facility with machines details
    @GetMapping("/{id}/with-machines")
    public ResponseEntity<FacilityDto> getFacilityWithMachines(@PathVariable String id) {
        try {
            Optional<FacilityDto> facility = facilityService.getFacilityWithMachines(id);
            return facility.map(facilityDto -> new ResponseEntity<>(facilityDto, HttpStatus.OK))
                          .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update facility
    @PutMapping("/{id}")
    public ResponseEntity<FacilityDto> updateFacility(@PathVariable String id, @Valid @RequestBody FacilityDto facilityDto) {
        try {
            FacilityDto updatedFacility = facilityService.updateFacility(id, facilityDto);
            return new ResponseEntity<>(updatedFacility, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete facility (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacility(@PathVariable String id) {
        try {
            facilityService.deleteFacility(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Reactivate facility
    @PutMapping("/{id}/reactivate")
    public ResponseEntity<FacilityDto> reactivateFacility(@PathVariable String id) {
        try {
            FacilityDto reactivatedFacility = facilityService.reactivateFacility(id);
            return new ResponseEntity<>(reactivatedFacility, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Check if facility exists
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> facilityExists(@PathVariable String id) {
        try {
            boolean exists = facilityService.facilityExists(id);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Check if facility name exists
    @GetMapping("/name/{name}/exists")
    public ResponseEntity<Boolean> facilityNameExists(@PathVariable String name) {
        try {
            boolean exists = facilityService.facilityNameExists(name);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get recently created facilities
    @GetMapping("/recent")
    public ResponseEntity<List<FacilityDto>> getRecentlyCreatedFacilities(@RequestParam(defaultValue = "24") int hours) {
        try {
            List<FacilityDto> facilities = facilityService.getRecentlyCreatedFacilities(hours);
            return new ResponseEntity<>(facilities, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get facilities by machine count range
    @GetMapping("/machine-count")
    public ResponseEntity<List<FacilityDto>> getFacilitiesByMachineCountRange(
            @RequestParam(required = false) Long minCount,
            @RequestParam(required = false) Long maxCount) {
        try {
            List<FacilityDto> facilities = facilityService.getFacilitiesByMachineCountRange(minCount, maxCount);
            return new ResponseEntity<>(facilities, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}