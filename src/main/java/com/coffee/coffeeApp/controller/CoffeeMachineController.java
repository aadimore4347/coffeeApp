package com.coffee.coffeeApp.controller;

import com.coffee.coffeeApp.dto.BrewCommandDto;
import com.coffee.coffeeApp.dto.CoffeeMachineDto;
import com.coffee.coffeeApp.service.CoffeeMachineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/machines")
@CrossOrigin(origins = "*")
public class CoffeeMachineController {

    @Autowired
    private CoffeeMachineService coffeeMachineService;

    // Create new coffee machine
    @PostMapping
    public ResponseEntity<CoffeeMachineDto> createCoffeeMachine(@Valid @RequestBody CoffeeMachineDto machineDto) {
        try {
            CoffeeMachineDto createdMachine = coffeeMachineService.createCoffeeMachine(machineDto);
            return new ResponseEntity<>(createdMachine, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get machine by ID
    @GetMapping("/{id}")
    public ResponseEntity<CoffeeMachineDto> getMachineById(@PathVariable String id) {
        try {
            Optional<CoffeeMachineDto> machine = coffeeMachineService.getMachineById(id);
            return machine.map(machineDto -> new ResponseEntity<>(machineDto, HttpStatus.OK))
                         .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get machines by facility ID
    @GetMapping("/facility/{facilityId}")
    public ResponseEntity<List<CoffeeMachineDto>> getMachinesByFacilityId(@PathVariable String facilityId) {
        try {
            List<CoffeeMachineDto> machines = coffeeMachineService.getMachinesByFacilityId(facilityId);
            return new ResponseEntity<>(machines, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all machines
    @GetMapping
    public ResponseEntity<List<CoffeeMachineDto>> getAllMachines() {
        try {
            List<CoffeeMachineDto> machines = coffeeMachineService.getAllMachines();
            return new ResponseEntity<>(machines, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get machines by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CoffeeMachineDto>> getMachinesByStatus(@PathVariable String status) {
        try {
            List<CoffeeMachineDto> machines = coffeeMachineService.getMachinesByStatus(status);
            return new ResponseEntity<>(machines, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get operational machines
    @GetMapping("/operational")
    public ResponseEntity<List<CoffeeMachineDto>> getOperationalMachines() {
        try {
            List<CoffeeMachineDto> machines = coffeeMachineService.getOperationalMachines();
            return new ResponseEntity<>(machines, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get machines with low supplies
    @GetMapping("/low-supplies")
    public ResponseEntity<List<CoffeeMachineDto>> getMachinesWithLowSupplies() {
        try {
            List<CoffeeMachineDto> machines = coffeeMachineService.getMachinesWithLowSupplies();
            return new ResponseEntity<>(machines, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get machines with critical supplies
    @GetMapping("/critical-supplies")
    public ResponseEntity<List<CoffeeMachineDto>> getMachinesWithCriticalSupplies() {
        try {
            List<CoffeeMachineDto> machines = coffeeMachineService.getMachinesWithCriticalSupplies();
            return new ResponseEntity<>(machines, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get machines needing maintenance
    @GetMapping("/maintenance-needed")
    public ResponseEntity<List<CoffeeMachineDto>> getMachinesNeedingMaintenance() {
        try {
            List<CoffeeMachineDto> machines = coffeeMachineService.getMachinesNeedingMaintenance();
            return new ResponseEntity<>(machines, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update machine levels (water, milk, beans)
    @PutMapping("/{id}/levels")
    public ResponseEntity<CoffeeMachineDto> updateMachineLevels(
            @PathVariable String id,
            @RequestParam(required = false) Float waterLevel,
            @RequestParam(required = false) Float milkLevel,
            @RequestParam(required = false) Float beansLevel) {
        try {
            CoffeeMachineDto updatedMachine = coffeeMachineService.updateMachineLevels(id, waterLevel, milkLevel, beansLevel, null);
            return new ResponseEntity<>(updatedMachine, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update machine status
    @PutMapping("/{id}/status")
    public ResponseEntity<CoffeeMachineDto> updateMachineStatus(
            @PathVariable String id,
            @RequestParam String status) {
        try {
            CoffeeMachineDto updatedMachine = coffeeMachineService.updateMachineStatus(id, status);
            return new ResponseEntity<>(updatedMachine, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Brew coffee
    @PostMapping("/brew")
    public ResponseEntity<String> brewCoffee(@Valid @RequestBody BrewCommandDto brewCommand) {
        try {
            // Use reflection to call the processBrew method since BrewResult is an inner class
            Object result = coffeeMachineService.getClass()
                .getMethod("processBrew", BrewCommandDto.class)
                .invoke(coffeeMachineService, brewCommand);
            
            // Get success and message from BrewResult
            boolean success = (boolean) result.getClass().getMethod("isSuccess").invoke(result);
            String message = (String) result.getClass().getMethod("getMessage").invoke(result);
            
            if (success) {
                return new ResponseEntity<>(message, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Brewing failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update machine
    @PutMapping("/{id}")
    public ResponseEntity<CoffeeMachineDto> updateMachine(@PathVariable String id, @Valid @RequestBody CoffeeMachineDto machineDto) {
        try {
            CoffeeMachineDto updatedMachine = coffeeMachineService.updateMachine(id, machineDto);
            return new ResponseEntity<>(updatedMachine, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete machine (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMachine(@PathVariable String id) {
        try {
            coffeeMachineService.deleteMachine(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Turn machine on
    @PutMapping("/{id}/turn-on")
    public ResponseEntity<CoffeeMachineDto> turnMachineOn(@PathVariable String id) {
        try {
            CoffeeMachineDto updatedMachine = coffeeMachineService.updateMachineStatus(id, "ON");
            return new ResponseEntity<>(updatedMachine, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Turn machine off
    @PutMapping("/{id}/turn-off")
    public ResponseEntity<CoffeeMachineDto> turnMachineOff(@PathVariable String id) {
        try {
            CoffeeMachineDto updatedMachine = coffeeMachineService.updateMachineStatus(id, "OFF");
            return new ResponseEntity<>(updatedMachine, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Refill all supplies
    @PutMapping("/{id}/refill")
    public ResponseEntity<CoffeeMachineDto> refillMachine(@PathVariable String id) {
        try {
            CoffeeMachineDto updatedMachine = coffeeMachineService.updateMachineLevels(id, 100.0f, 100.0f, 100.0f, null);
            return new ResponseEntity<>(updatedMachine, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}