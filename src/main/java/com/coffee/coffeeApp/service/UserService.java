package com.coffee.coffeeApp.service;

import com.coffee.coffeeApp.dto.UserDto;
import com.coffee.coffeeApp.entity.User;
import com.coffee.coffeeApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // Create new user
    public UserDto createUser(UserDto userDto) {
        validateUserDto(userDto);
        
        // Check if username already exists
        if (userRepository.existsByUsernameAndIsActiveTrue(userDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + userDto.getUsername());
        }
        
        User user = convertToEntity(userDto);
        user.setIsActive(true);
        
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }
    
    // Get user by ID
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .filter(User::isActive)
                .map(this::convertToDto);
    }
    
    // Get user by username
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsernameAndIsActiveTrue(username)
                .map(this::convertToDto);
    }
    
    // Get all users
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get users by role
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByRole(String role) {
        return userRepository.findByRoleAndIsActiveTrue(role)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get facility users
    @Transactional(readOnly = true)
    public List<UserDto> getFacilityUsers() {
        return userRepository.findFacilityUsers()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get admin users
    @Transactional(readOnly = true)
    public List<UserDto> getAdminUsers() {
        return userRepository.findAdminUsers()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Update user
    public UserDto updateUser(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .filter(User::isActive)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        
        validateUserDto(userDto);
        
        // Check if username already exists (excluding current user)
        if (!existingUser.getUsername().equals(userDto.getUsername()) &&
            userRepository.existsByUsernameAndIsActiveTrue(userDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + userDto.getUsername());
        }
        
        // Update fields
        existingUser.setUsername(userDto.getUsername());
        existingUser.setRole(userDto.getRole());
        
        User updatedUser = userRepository.save(existingUser);
        return convertToDto(updatedUser);
    }
    
    // Delete user (soft delete)
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .filter(User::isActive)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        
        user.setIsActive(false);
        userRepository.save(user);
    }
    
    // Reactivate user
    public UserDto reactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        
        user.setIsActive(true);
        User reactivatedUser = userRepository.save(user);
        return convertToDto(reactivatedUser);
    }
    
    // Check if user exists
    @Transactional(readOnly = true)
    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }
    
    // Check if username exists
    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {
        return userRepository.existsByUsernameAndIsActiveTrue(username);
    }
    
    // Get recently created users
    @Transactional(readOnly = true)
    public List<UserDto> getRecentlyCreatedUsers(int hours) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hours);
        return userRepository.findRecentlyCreatedUsers(cutoffTime)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Validation helper
    private void validateUserDto(UserDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("User data cannot be null");
        }
        if (userDto.getUsername() == null || userDto.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (userDto.getRole() == null || userDto.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Role is required");
        }
        if (!"FACILITY".equals(userDto.getRole()) && !"ADMIN".equals(userDto.getRole())) {
            throw new IllegalArgumentException("Role must be either FACILITY or ADMIN");
        }
    }
    
    // Convert entity to DTO
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        dto.setCreationDate(user.getCreationDate());
        dto.setLastUpdate(user.getLastUpdate());
        return dto;
    }
    
    // Convert DTO to entity
    private User convertToEntity(UserDto dto) {
        User user = new User();
        if (dto.getId() != null) {
            user.setId(dto.getId());
        }
        user.setUsername(dto.getUsername());
        user.setRole(dto.getRole());
        user.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return user;
    }
}