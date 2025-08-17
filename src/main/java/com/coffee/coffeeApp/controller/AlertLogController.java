package com.coffee.coffeeApp.controller;

import com.coffee.coffeeApp.dto.AlertLogDto;
import com.coffee.coffeeApp.service.AlertLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertLogController {

    @Autowired
    private AlertLogService alertLogService;

    // Create generic alert
    @PostMapping
    public ResponseEntity<AlertLogDto> createAlert(
            @RequestParam String machineId,
            @RequestParam String alertType,
            @RequestParam String message) {
        try {
            AlertLogDto createdAlert = alertLogService.createAlert(machineId, alertType, message);
            return new ResponseEntity<>(createdAlert, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Create low water alert
    @PostMapping("/low-water")
    public ResponseEntity<AlertLogDto> createLowWaterAlert(
            @RequestParam String machineId,
            @RequestParam float currentLevel) {
        try {
            AlertLogDto createdAlert = alertLogService.createLowWaterAlert(machineId, currentLevel);
            return new ResponseEntity<>(createdAlert, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Create low milk alert
    @PostMapping("/low-milk")
    public ResponseEntity<AlertLogDto> createLowMilkAlert(
            @RequestParam String machineId,
            @RequestParam float currentLevel) {
        try {
            AlertLogDto createdAlert = alertLogService.createLowMilkAlert(machineId, currentLevel);
            return new ResponseEntity<>(createdAlert, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Create low beans alert
    @PostMapping("/low-beans")
    public ResponseEntity<AlertLogDto> createLowBeansAlert(
            @RequestParam String machineId,
            @RequestParam float currentLevel) {
        try {
            AlertLogDto createdAlert = alertLogService.createLowBeansAlert(machineId, currentLevel);
            return new ResponseEntity<>(createdAlert, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Create malfunction alert
    @PostMapping("/malfunction")
    public ResponseEntity<AlertLogDto> createMalfunctionAlert(
            @RequestParam String machineId,
            @RequestParam String issue) {
        try {
            AlertLogDto createdAlert = alertLogService.createMalfunctionAlert(machineId, issue);
            return new ResponseEntity<>(createdAlert, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Create offline alert
    @PostMapping("/offline")
    public ResponseEntity<AlertLogDto> createOfflineAlert(
            @RequestParam String machineId,
            @RequestParam String reason) {
        try {
            AlertLogDto createdAlert = alertLogService.createOfflineAlert(machineId, reason);
            return new ResponseEntity<>(createdAlert, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Create bulk alerts
    @PostMapping("/bulk")
    public ResponseEntity<List<AlertLogDto>> createBulkAlerts(@Valid @RequestBody List<AlertLogDto> alertDtos) {
        try {
            List<AlertLogDto> createdAlerts = alertLogService.createBulkAlerts(alertDtos);
            return new ResponseEntity<>(createdAlerts, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get alert by ID
    @GetMapping("/{id}")
    public ResponseEntity<AlertLogDto> getAlertById(@PathVariable String id) {
        try {
            Optional<AlertLogDto> alert = alertLogService.getAlertById(id);
            return alert.map(alertDto -> new ResponseEntity<>(alertDto, HttpStatus.OK))
                       .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all active alerts
    @GetMapping("/active")
    public ResponseEntity<List<AlertLogDto>> getAllActiveAlerts() {
        try {
            List<AlertLogDto> alerts = alertLogService.getAllActiveAlerts();
            return new ResponseEntity<>(alerts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get alerts by machine
    @GetMapping("/machine/{machineId}")
    public ResponseEntity<List<AlertLogDto>> getAlertsByMachine(@PathVariable String machineId) {
        try {
            List<AlertLogDto> alerts = alertLogService.getAlertsByMachine(machineId);
            return new ResponseEntity<>(alerts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get alerts by type
    @GetMapping("/type/{alertType}")
    public ResponseEntity<List<AlertLogDto>> getAlertsByType(@PathVariable String alertType) {
        try {
            List<AlertLogDto> alerts = alertLogService.getAlertsByType(alertType);
            return new ResponseEntity<>(alerts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get recent alerts
    @GetMapping("/recent")
    public ResponseEntity<List<AlertLogDto>> getRecentAlerts(@RequestParam(defaultValue = "24") int hours) {
        try {
            List<AlertLogDto> alerts = alertLogService.getRecentAlerts(hours);
            return new ResponseEntity<>(alerts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get critical alerts
    @GetMapping("/critical")
    public ResponseEntity<List<AlertLogDto>> getCriticalAlerts() {
        try {
            List<AlertLogDto> alerts = alertLogService.getCriticalAlerts();
            return new ResponseEntity<>(alerts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get supply alerts
    @GetMapping("/supply")
    public ResponseEntity<List<AlertLogDto>> getSupplyAlerts() {
        try {
            List<AlertLogDto> alerts = alertLogService.getSupplyAlerts();
            return new ResponseEntity<>(alerts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get today's alerts
    @GetMapping("/today")
    public ResponseEntity<List<AlertLogDto>> getTodayAlerts() {
        try {
            List<AlertLogDto> alerts = alertLogService.getTodayAlerts();
            return new ResponseEntity<>(alerts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get unresolved alerts
    @GetMapping("/unresolved")
    public ResponseEntity<List<AlertLogDto>> getUnresolvedAlerts(@RequestParam(defaultValue = "72") int hours) {
        try {
            List<AlertLogDto> alerts = alertLogService.getUnresolvedAlerts(hours);
            return new ResponseEntity<>(alerts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get alerts requiring attention
    @GetMapping("/attention-required")
    public ResponseEntity<List<AlertLogDto>> getAlertsRequiringAttention() {
        try {
            List<AlertLogDto> alerts = alertLogService.getAlertsRequiringAttention();
            return new ResponseEntity<>(alerts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Resolve alert by ID
    @PutMapping("/{id}/resolve")
    public ResponseEntity<Void> resolveAlert(@PathVariable String id) {
        try {
            alertLogService.resolveAlert(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Resolve alerts by machine and type
    @PutMapping("/resolve/machine/{machineId}/type/{alertType}")
    public ResponseEntity<Void> resolveAlertsByMachineAndType(
            @PathVariable String machineId,
            @PathVariable String alertType) {
        try {
            alertLogService.resolveAlertsByMachineAndType(machineId, alertType);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Check for duplicate alert
    @GetMapping("/duplicate-check")
    public ResponseEntity<Boolean> hasDuplicateAlert(
            @RequestParam String machineId,
            @RequestParam String alertType,
            @RequestParam(defaultValue = "30") int withinMinutes) {
        try {
            boolean hasDuplicate = alertLogService.hasDuplicateAlert(machineId, alertType, withinMinutes);
            return new ResponseEntity<>(hasDuplicate, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get alert statistics
    @GetMapping("/stats")
    public ResponseEntity<Object> getAlertStatistics() {
        try {
            Object stats = alertLogService.getClass()
                .getMethod("getAlertStatistics")
                .invoke(alertLogService);
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get alert statistics by machine
    @GetMapping("/stats/machine/{machineId}")
    public ResponseEntity<Object> getAlertStatisticsByMachine(@PathVariable String machineId) {
        try {
            Object stats = alertLogService.getClass()
                .getMethod("getAlertStatisticsByMachine", String.class)
                .invoke(alertLogService, machineId);
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}