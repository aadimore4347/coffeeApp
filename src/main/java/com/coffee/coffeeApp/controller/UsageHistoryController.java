package com.coffee.coffeeApp.controller;

import com.coffee.coffeeApp.dto.UsageHistoryDto;
import com.coffee.coffeeApp.service.UsageHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/usage")
@CrossOrigin(origins = "*")
public class UsageHistoryController {

    @Autowired
    private UsageHistoryService usageHistoryService;

    @PostMapping
    public ResponseEntity<UsageHistoryDto> createUsageRecord(@Valid @RequestBody UsageHistoryDto usageDto) {
        UsageHistoryDto createdUsage = usageHistoryService.createUsageRecord(usageDto);
        return ResponseEntity.ok(createdUsage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsageHistoryDto> getUsageById(@PathVariable String id) {
        return usageHistoryService.getUsageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UsageHistoryDto>> getAllUsage() {
        List<UsageHistoryDto> usage = usageHistoryService.getAllUsage();
        return ResponseEntity.ok(usage);
    }

    @GetMapping("/machine/{machineId}")
    public ResponseEntity<List<UsageHistoryDto>> getUsageByMachine(@PathVariable String machineId) {
        List<UsageHistoryDto> usage = usageHistoryService.getUsageByMachine(machineId);
        return ResponseEntity.ok(usage);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UsageHistoryDto>> getUsageByUser(@PathVariable String userId) {
        List<UsageHistoryDto> usage = usageHistoryService.getUsageByUser(userId);
        return ResponseEntity.ok(usage);
    }

    @GetMapping("/brew-type/{brewType}")
    public ResponseEntity<List<UsageHistoryDto>> getUsageByBrewType(@PathVariable String brewType) {
        List<UsageHistoryDto> usage = usageHistoryService.getUsageByBrewType(brewType);
        return ResponseEntity.ok(usage);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<UsageHistoryDto>> getUsageByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<UsageHistoryDto> usage = usageHistoryService.getUsageByDateRange(startDate, endDate);
        return ResponseEntity.ok(usage);
    }

    @GetMapping("/today")
    public ResponseEntity<List<UsageHistoryDto>> getTodayUsage() {
        List<UsageHistoryDto> usage = usageHistoryService.getTodayUsage();
        return ResponseEntity.ok(usage);
    }
}