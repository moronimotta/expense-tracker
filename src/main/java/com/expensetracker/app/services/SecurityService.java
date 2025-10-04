package com.expensetracker.app.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.expensetracker.app.repositories.UserRepository;
import com.expensetracker.app.models.User;
import com.expensetracker.app.models.enums.UserRole;

@Service
public class SecurityService {

    @Autowired
    private UserRepository userRepository;

    // In a real application, this would get the current user from JWT token or session
    private String currentUserId = null;

    public void setCurrentUserId(String userId) {
        this.currentUserId = userId;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public User getCurrentUser() {
        if (currentUserId == null) {
            throw new SecurityException("No authenticated user");
        }
        try {
            User user = userRepository.findById(currentUserId);
            if (user == null) {
                throw new SecurityException("Current user not found");
            }
            return user;
        } catch (Exception e) {
            throw new SecurityException("Error retrieving current user: " + e.getMessage());
        }
    }

    public boolean isCurrentUserAdmin() {
        try {
            User currentUser = getCurrentUser();
            return currentUser.getRole() == UserRole.ADMIN;
        } catch (SecurityException e) {
            return false;
        }
    }

    public boolean isAuthorized(String requestedUserId) {
        if (currentUserId == null) {
            throw new SecurityException("No authenticated user");
        }
        
        // Admin can access any user's data
        if (isCurrentUserAdmin()) {
            return true;
        }
        
        // Regular users can only access their own data
        return currentUserId.equals(requestedUserId);
    }

    public void validateUserAccess(String requestedUserId) {
        if (!isAuthorized(requestedUserId)) {
            throw new SecurityException("Access denied: Cannot access resources for user " + requestedUserId);
        }
    }

    public void requireAdmin() {
        if (!isCurrentUserAdmin()) {
            throw new SecurityException("Access denied: Admin role required");
        }
    }

    public void requireAuthentication() {
        if (currentUserId == null) {
            throw new SecurityException("Authentication required");
        }
    }
}