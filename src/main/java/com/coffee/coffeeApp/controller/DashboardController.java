package com.coffee.coffeeApp.controller;

import com.coffee.coffeeApp.dto.*;
import com.coffee.coffeeApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private CoffeeMachineService coffeeMachineService;

    @Autowired
    private AlertLogService alertLogService;

    @Autowired
    private UsageHistoryService usageHistoryService;

    @Autowired
    private UserService userService;

    // Get admin dashboard summary
    @GetMapping("/admin")
    public ResponseEntity<DashboardSummaryDto> getAdminDashboard() {
        try {
            DashboardSummaryDto dashboard = DashboardSummaryDto.createAdminDashboard();
            
            // Get all facilities and machines
            List<FacilityDto> facilities = facilityService.getAllFacilities();
            List<CoffeeMachineDto> machines = coffeeMachineService.getAllMachines();
            
            // Basic statistics
            dashboard.setTotalFacilities((long) facilities.size());
            dashboard.setTotalMachines((long) machines.size());
            dashboard.setActiveMachines(machines.stream().filter(m -> "ON".equals(m.getStatus())).count());
            dashboard.setOperationalMachines((long) coffeeMachineService.getOperationalMachines().size());
            
            // Alert statistics
            List<AlertLogDto> activeAlerts = alertLogService.getAllActiveAlerts();
            dashboard.setTotalAlerts((long) activeAlerts.size());
            dashboard.setCriticalAlerts((long) alertLogService.getCriticalAlerts().size());
            dashboard.setSupplyAlerts((long) alertLogService.getSupplyAlerts().size());
            dashboard.setTodayAlerts((long) alertLogService.getTodayAlerts().size());
            dashboard.setUnresolvedAlerts((long) alertLogService.getUnresolvedAlerts(72).size());
            
            // Usage statistics
            List<UsageHistoryDto> todayUsage = usageHistoryService.getTodayUsage();
            dashboard.setTotalUsageToday((long) todayUsage.size());
            dashboard.setTotalUsageThisWeek((long) usageHistoryService.getRecentUsage(7 * 24).size());
            dashboard.setTotalUsageThisMonth((long) usageHistoryService.getRecentUsage(30 * 24).size());
            
            // Supply level statistics
            if (!machines.isEmpty()) {
                dashboard.setAverageWaterLevel(machines.stream()
                    .filter(m -> m.getWaterLevel() != null)
                    .mapToDouble(CoffeeMachineDto::getWaterLevel)
                    .average().orElse(0.0));
                dashboard.setAverageMilkLevel(machines.stream()
                    .filter(m -> m.getMilkLevel() != null)
                    .mapToDouble(CoffeeMachineDto::getMilkLevel)
                    .average().orElse(0.0));
                dashboard.setAverageBeansLevel(machines.stream()
                    .filter(m -> m.getBeansLevel() != null)
                    .mapToDouble(CoffeeMachineDto::getBeansLevel)
                    .average().orElse(0.0));
            }
            
            dashboard.setMachinesWithLowWater(machines.stream()
                .filter(m -> m.getWaterLevel() != null && m.getWaterLevel() < 20.0f)
                .count());
            dashboard.setMachinesWithLowMilk(machines.stream()
                .filter(m -> m.getMilkLevel() != null && m.getMilkLevel() < 20.0f)
                .count());
            dashboard.setMachinesWithLowBeans(machines.stream()
                .filter(m -> m.getBeansLevel() != null && m.getBeansLevel() < 20.0f)
                .count());
            
            // Performance metrics
            long totalMachines = dashboard.getTotalMachines() != null ? dashboard.getTotalMachines() : 0;
            long operationalMachines = dashboard.getOperationalMachines() != null ? dashboard.getOperationalMachines() : 0;
            dashboard.setSystemUptime(totalMachines > 0 ? (operationalMachines * 100.0) / totalMachines : 0.0);
            
            // Detailed breakdowns
            dashboard.setFacilitiesWithIssues(facilityService.getFacilitiesWithLowSupplyMachines());
            dashboard.setProblematicMachines(coffeeMachineService.getMachinesWithCriticalSupplies());
            dashboard.setRecentCriticalAlerts(alertLogService.getCriticalAlerts().stream()
                .limit(10)
                .collect(Collectors.toList()));
            
            return new ResponseEntity<>(dashboard, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get facility dashboard summary
    @GetMapping("/facility/{facilityId}")
    public ResponseEntity<DashboardSummaryDto> getFacilityDashboard(@PathVariable String facilityId) {
        try {
            FacilityDto facility = facilityService.getFacilityById(facilityId).orElse(null);
            if (facility == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            DashboardSummaryDto dashboard = DashboardSummaryDto.createFacilityDashboard(
                facilityId, facility.getName(), facility.getLocation());
            
            // Get facility machines
            List<CoffeeMachineDto> machines = coffeeMachineService.getMachinesByFacilityId(facilityId);
            dashboard.setFacilityMachines(machines);
            
            // Basic statistics
            dashboard.setTotalMachines((long) machines.size());
            dashboard.setActiveMachines(machines.stream().filter(m -> "ON".equals(m.getStatus())).count());
            dashboard.setOperationalMachines(machines.stream()
                .filter(m -> "ON".equals(m.getStatus()) && 
                           m.getWaterLevel() != null && m.getWaterLevel() > 10.0f &&
                           m.getMilkLevel() != null && m.getMilkLevel() > 10.0f &&
                           m.getBeansLevel() != null && m.getBeansLevel() > 10.0f)
                .count());
            
            // Alert statistics for this facility
            List<AlertLogDto> facilityAlerts = machines.stream()
                .flatMap(machine -> alertLogService.getAlertsByMachine(machine.getId()).stream())
                .collect(Collectors.toList());
            
            dashboard.setTotalAlerts((long) facilityAlerts.size());
            dashboard.setCriticalAlerts(facilityAlerts.stream()
                .filter(alert -> "CRITICAL".equals(alert.getSeverity()) || 
                               "MALFUNCTION".equals(alert.getAlertType()) ||
                               "OFFLINE".equals(alert.getAlertType()))
                .count());
            dashboard.setSupplyAlerts(facilityAlerts.stream()
                .filter(alert -> "LOW_WATER".equals(alert.getAlertType()) ||
                               "LOW_MILK".equals(alert.getAlertType()) ||
                               "LOW_BEANS".equals(alert.getAlertType()))
                .count());
            
            // Usage statistics for this facility
            List<UsageHistoryDto> facilityUsage = machines.stream()
                .flatMap(machine -> usageHistoryService.getTodayUsageByMachine(machine.getId()).stream())
                .collect(Collectors.toList());
            
            dashboard.setTotalUsageToday((long) facilityUsage.size());
            
            // Supply level statistics for this facility
            if (!machines.isEmpty()) {
                dashboard.setAverageWaterLevel(machines.stream()
                    .filter(m -> m.getWaterLevel() != null)
                    .mapToDouble(CoffeeMachineDto::getWaterLevel)
                    .average().orElse(0.0));
                dashboard.setAverageMilkLevel(machines.stream()
                    .filter(m -> m.getMilkLevel() != null)
                    .mapToDouble(CoffeeMachineDto::getMilkLevel)
                    .average().orElse(0.0));
                dashboard.setAverageBeansLevel(machines.stream()
                    .filter(m -> m.getBeansLevel() != null)
                    .mapToDouble(CoffeeMachineDto::getBeansLevel)
                    .average().orElse(0.0));
            }
            
            dashboard.setMachinesWithLowWater(machines.stream()
                .filter(m -> m.getWaterLevel() != null && m.getWaterLevel() < 20.0f)
                .count());
            dashboard.setMachinesWithLowMilk(machines.stream()
                .filter(m -> m.getMilkLevel() != null && m.getMilkLevel() < 20.0f)
                .count());
            dashboard.setMachinesWithLowBeans(machines.stream()
                .filter(m -> m.getBeansLevel() != null && m.getBeansLevel() < 20.0f)
                .count());
            
            // Performance metrics
            long totalMachines = dashboard.getTotalMachines() != null ? dashboard.getTotalMachines() : 0;
            long operationalMachines = dashboard.getOperationalMachines() != null ? dashboard.getOperationalMachines() : 0;
            dashboard.setSystemUptime(totalMachines > 0 ? (operationalMachines * 100.0) / totalMachines : 0.0);
            
            return new ResponseEntity<>(dashboard, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get system health overview
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        try {
            Map<String, Object> health = new HashMap<>();
            
            // Machine health
            List<CoffeeMachineDto> allMachines = coffeeMachineService.getAllMachines();
            long operationalMachines = coffeeMachineService.getOperationalMachines().size();
            long totalMachines = allMachines.size();
            
            health.put("totalMachines", totalMachines);
            health.put("operationalMachines", operationalMachines);
            health.put("machinesWithIssues", totalMachines - operationalMachines);
            health.put("systemUptime", totalMachines > 0 ? (operationalMachines * 100.0) / totalMachines : 0.0);
            
            // Alert health
            health.put("criticalAlerts", alertLogService.getCriticalAlerts().size());
            health.put("totalActiveAlerts", alertLogService.getAllActiveAlerts().size());
            
            // Supply health
            health.put("machinesWithLowSupplies", coffeeMachineService.getMachinesWithLowSupplies().size());
            health.put("machinesWithCriticalSupplies", coffeeMachineService.getMachinesWithCriticalSupplies().size());
            
            // Usage health
            health.put("todayUsage", usageHistoryService.getTodayUsage().size());
            health.put("recentUsage", usageHistoryService.getRecentUsage(24).size());
            
            health.put("lastUpdated", LocalDateTime.now());
            
            return new ResponseEntity<>(health, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get usage analytics
    @GetMapping("/analytics/usage")
    public ResponseEntity<Map<String, Object>> getUsageAnalytics() {
        try {
            Map<String, Object> analytics = new HashMap<>();
            
            // Use reflection to get analytics data from services
            analytics.put("brewTypePopularity", 
                usageHistoryService.getClass()
                    .getMethod("getBrewTypePopularity")
                    .invoke(usageHistoryService));
            
            analytics.put("hourlyPatterns", 
                usageHistoryService.getClass()
                    .getMethod("getHourlyUsagePatterns")
                    .invoke(usageHistoryService));
            
            analytics.put("todayUsage", usageHistoryService.getTodayUsage().size());
            analytics.put("weeklyUsage", usageHistoryService.getRecentUsage(7 * 24).size());
            analytics.put("monthlyUsage", usageHistoryService.getRecentUsage(30 * 24).size());
            analytics.put("lastUpdated", LocalDateTime.now());
            
            return new ResponseEntity<>(analytics, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get machine performance analytics
    @GetMapping("/analytics/machines")
    public ResponseEntity<Map<String, Object>> getMachineAnalytics() {
        try {
            Map<String, Object> analytics = new HashMap<>();
            
            List<CoffeeMachineDto> machines = coffeeMachineService.getAllMachines();
            
            analytics.put("totalMachines", machines.size());
            analytics.put("operationalMachines", coffeeMachineService.getOperationalMachines().size());
            analytics.put("machinesWithLowSupplies", coffeeMachineService.getMachinesWithLowSupplies().size());
            analytics.put("machinesNeedingMaintenance", coffeeMachineService.getMachinesNeedingMaintenance().size());
            
            // Machine status distribution
            Map<String, Long> statusDistribution = machines.stream()
                .collect(Collectors.groupingBy(CoffeeMachineDto::getStatus, Collectors.counting()));
            analytics.put("statusDistribution", statusDistribution);
            
            // Supply level averages
            if (!machines.isEmpty()) {
                analytics.put("averageWaterLevel", machines.stream()
                    .filter(m -> m.getWaterLevel() != null)
                    .mapToDouble(CoffeeMachineDto::getWaterLevel)
                    .average().orElse(0.0));
                analytics.put("averageMilkLevel", machines.stream()
                    .filter(m -> m.getMilkLevel() != null)
                    .mapToDouble(CoffeeMachineDto::getMilkLevel)
                    .average().orElse(0.0));
                analytics.put("averageBeansLevel", machines.stream()
                    .filter(m -> m.getBeansLevel() != null)
                    .mapToDouble(CoffeeMachineDto::getBeansLevel)
                    .average().orElse(0.0));
            }
            
            analytics.put("lastUpdated", LocalDateTime.now());
            
            return new ResponseEntity<>(analytics, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get alert analytics
    @GetMapping("/analytics/alerts")
    public ResponseEntity<Map<String, Object>> getAlertAnalytics() {
        try {
            Map<String, Object> analytics = new HashMap<>();
            
            List<AlertLogDto> activeAlerts = alertLogService.getAllActiveAlerts();
            List<AlertLogDto> todayAlerts = alertLogService.getTodayAlerts();
            List<AlertLogDto> criticalAlerts = alertLogService.getCriticalAlerts();
            
            analytics.put("totalActiveAlerts", activeAlerts.size());
            analytics.put("todayAlerts", todayAlerts.size());
            analytics.put("criticalAlerts", criticalAlerts.size());
            analytics.put("supplyAlerts", alertLogService.getSupplyAlerts().size());
            
            // Alert type distribution
            Map<String, Long> typeDistribution = activeAlerts.stream()
                .collect(Collectors.groupingBy(AlertLogDto::getAlertType, Collectors.counting()));
            analytics.put("alertTypeDistribution", typeDistribution);
            
            analytics.put("lastUpdated", LocalDateTime.now());
            
            return new ResponseEntity<>(analytics, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}