package com.coffee.coffeeApp.controller;

import com.coffee.coffeeApp.dto.UserDto;
import com.coffee.coffeeApp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // Create new user
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        try {
            UserDto createdUser = userService.createUser(userDto);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String id) {
        try {
            Optional<UserDto> user = userService.getUserById(id);
            return user.map(userDto -> new ResponseEntity<>(userDto, HttpStatus.OK))
                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get user by username
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        try {
            Optional<UserDto> user = userService.getUserByUsername(username);
            return user.map(userDto -> new ResponseEntity<>(userDto, HttpStatus.OK))
                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            List<UserDto> users = userService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get users by role
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable String role) {
        try {
            List<UserDto> users = userService.getUsersByRole(role);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get facility users
    @GetMapping("/facility")
    public ResponseEntity<List<UserDto>> getFacilityUsers() {
        try {
            List<UserDto> users = userService.getFacilityUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get admin users
    @GetMapping("/admin")
    public ResponseEntity<List<UserDto>> getAdminUsers() {
        try {
            List<UserDto> users = userService.getAdminUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable String id, @Valid @RequestBody UserDto userDto) {
        try {
            UserDto updatedUser = userService.updateUser(id, userDto);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete user (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Reactivate user
    @PutMapping("/{id}/reactivate")
    public ResponseEntity<UserDto> reactivateUser(@PathVariable String id) {
        try {
            UserDto reactivatedUser = userService.reactivateUser(id);
            return new ResponseEntity<>(reactivatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Check if user exists
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> userExists(@PathVariable String id) {
        try {
            boolean exists = userService.userExists(id);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Check if username exists
    @GetMapping("/username/{username}/exists")
    public ResponseEntity<Boolean> usernameExists(@PathVariable String username) {
        try {
            boolean exists = userService.usernameExists(username);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get recently created users
    @GetMapping("/recent")
    public ResponseEntity<List<UserDto>> getRecentlyCreatedUsers(@RequestParam(defaultValue = "24") int hours) {
        try {
            List<UserDto> users = userService.getRecentlyCreatedUsers(hours);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}