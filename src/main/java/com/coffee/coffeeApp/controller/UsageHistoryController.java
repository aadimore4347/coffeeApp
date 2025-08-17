package com.coffee.coffeeApp.controller;

import com.coffee.coffeeApp.dto.UsageHistoryDto;
import com.coffee.coffeeApp.service.UsageHistoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usage-history")
@CrossOrigin(origins = "*")
public class UsageHistoryController {

    @Autowired
    private UsageHistoryService usageHistoryService;

    // Create usage record
    @PostMapping
    public ResponseEntity<UsageHistoryDto> createUsageRecord(@Valid @RequestBody UsageHistoryDto usageDto) {
        try {
            UsageHistoryDto createdUsage = usageHistoryService.createUsageRecord(usageDto);
            return new ResponseEntity<>(createdUsage, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Create usage record with simple parameters
    @PostMapping("/simple")
    public ResponseEntity<UsageHistoryDto> createUsageRecord(
            @RequestParam String machineId,
            @RequestParam String brewType,
            @RequestParam String userId) {
        try {
            UsageHistoryDto createdUsage = usageHistoryService.createUsageRecord(machineId, brewType, userId);
            return new ResponseEntity<>(createdUsage, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get usage by ID
    @GetMapping("/{id}")
    public ResponseEntity<UsageHistoryDto> getUsageById(@PathVariable String id) {
        try {
            Optional<UsageHistoryDto> usage = usageHistoryService.getUsageById(id);
            return usage.map(usageDto -> new ResponseEntity<>(usageDto, HttpStatus.OK))
                       .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all usage records
    @GetMapping
    public ResponseEntity<List<UsageHistoryDto>> getAllUsage() {
        try {
            List<UsageHistoryDto> usageRecords = usageHistoryService.getAllUsage();
            return new ResponseEntity<>(usageRecords, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get usage by machine
    @GetMapping("/machine/{machineId}")
    public ResponseEntity<List<UsageHistoryDto>> getUsageByMachine(@PathVariable String machineId) {
        try {
            List<UsageHistoryDto> usageRecords = usageHistoryService.getUsageByMachine(machineId);
            return new ResponseEntity<>(usageRecords, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get usage by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UsageHistoryDto>> getUsageByUser(@PathVariable String userId) {
        try {
            List<UsageHistoryDto> usageRecords = usageHistoryService.getUsageByUser(userId);
            return new ResponseEntity<>(usageRecords, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get usage by brew type
    @GetMapping("/brew-type/{brewType}")
    public ResponseEntity<List<UsageHistoryDto>> getUsageByBrewType(@PathVariable String brewType) {
        try {
            List<UsageHistoryDto> usageRecords = usageHistoryService.getUsageByBrewType(brewType);
            return new ResponseEntity<>(usageRecords, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get usage by date range
    @GetMapping("/date-range")
    public ResponseEntity<List<UsageHistoryDto>> getUsageByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<UsageHistoryDto> usageRecords = usageHistoryService.getUsageByDateRange(startDate, endDate);
            return new ResponseEntity<>(usageRecords, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get usage by machine and date range
    @GetMapping("/machine/{machineId}/date-range")
    public ResponseEntity<List<UsageHistoryDto>> getUsageByMachineAndDateRange(
            @PathVariable String machineId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<UsageHistoryDto> usageRecords = usageHistoryService.getUsageByMachineAndDateRange(machineId, startDate, endDate);
            return new ResponseEntity<>(usageRecords, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get today's usage
    @GetMapping("/today")
    public ResponseEntity<List<UsageHistoryDto>> getTodayUsage() {
        try {
            List<UsageHistoryDto> usageRecords = usageHistoryService.getTodayUsage();
            return new ResponseEntity<>(usageRecords, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get today's usage by machine
    @GetMapping("/machine/{machineId}/today")
    public ResponseEntity<List<UsageHistoryDto>> getTodayUsageByMachine(@PathVariable String machineId) {
        try {
            List<UsageHistoryDto> usageRecords = usageHistoryService.getTodayUsageByMachine(machineId);
            return new ResponseEntity<>(usageRecords, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get recent usage
    @GetMapping("/recent")
    public ResponseEntity<List<UsageHistoryDto>> getRecentUsage(@RequestParam(defaultValue = "24") int hours) {
        try {
            List<UsageHistoryDto> usageRecords = usageHistoryService.getRecentUsage(hours);
            return new ResponseEntity<>(usageRecords, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get usage statistics by machine
    @GetMapping("/stats/machine/{machineId}")
    public ResponseEntity<Map<String, Object>> getUsageStatsByMachine(@PathVariable String machineId) {
        try {
            // Use reflection to call the method since it returns Map<String, Object>
            Object stats = usageHistoryService.getClass()
                .getMethod("getUsageStatsByMachine", String.class)
                .invoke(usageHistoryService, machineId);
            @SuppressWarnings("unchecked")
            Map<String, Object> statsMap = (Map<String, Object>) stats;
            return new ResponseEntity<>(statsMap, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get usage statistics by user
    @GetMapping("/stats/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUsageStatsByUser(@PathVariable String userId) {
        try {
            Object stats = usageHistoryService.getClass()
                .getMethod("getUsageStatsByUser", String.class)
                .invoke(usageHistoryService, userId);
            @SuppressWarnings("unchecked")
            Map<String, Object> statsMap = (Map<String, Object>) stats;
            return new ResponseEntity<>(statsMap, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get brew type popularity
    @GetMapping("/stats/brew-types")
    public ResponseEntity<Map<String, Long>> getBrewTypePopularity() {
        try {
            Object stats = usageHistoryService.getClass()
                .getMethod("getBrewTypePopularity")
                .invoke(usageHistoryService);
            @SuppressWarnings("unchecked")
            Map<String, Long> statsMap = (Map<String, Long>) stats;
            return new ResponseEntity<>(statsMap, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get hourly usage patterns
    @GetMapping("/stats/hourly-patterns")
    public ResponseEntity<Map<Integer, Long>> getHourlyUsagePatterns() {
        try {
            Object stats = usageHistoryService.getClass()
                .getMethod("getHourlyUsagePatterns")
                .invoke(usageHistoryService);
            @SuppressWarnings("unchecked")
            Map<Integer, Long> statsMap = (Map<Integer, Long>) stats;
            return new ResponseEntity<>(statsMap, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete usage record
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsageRecord(@PathVariable String id) {
        try {
            usageHistoryService.deleteUsageRecord(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}