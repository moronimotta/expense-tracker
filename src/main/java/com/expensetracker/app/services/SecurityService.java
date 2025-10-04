package com.expensetracker.app.services;

import com.expensetracker.app.models.User;
import com.expensetracker.app.models.enums.UserRole;
import com.expensetracker.app.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final UserRepository userRepository;
    private final ThreadLocal<String> currentUserId = new ThreadLocal<>();

    public SecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // New methods for current user management
    public void setCurrentUser(String userId) {
        currentUserId.set(userId);
    }

    public void clearCurrentUser() {
        currentUserId.remove();
    }

    public String getCurrentUserId() {
        String userId = currentUserId.get();
        if (userId == null) {
            throw new SecurityException("No authenticated user");
        }
        return userId;
    }

    public User getCurrentUser() {
        String userId = getCurrentUserId();
        try {
            User user = userRepository.findById(userId);
            if (user == null) {
                throw new SecurityException("User not found: " + userId);
            }
            return user;
        } catch (Exception e) {
            throw new SecurityException("Error fetching user: " + e.getMessage());
        }
    }

    // Updated existing methods
    public void requireAdmin() {
        User user = getCurrentUser();
        if (user.getRole() != UserRole.ADMIN) {
            throw new SecurityException("Access denied: Admin role required");
        }
    }

    public void validateUserAccess(String targetUserId) {
        User currentUser = getCurrentUser();
        
        // Admin can access anyone's data
        if (currentUser.getRole() == UserRole.ADMIN) {
            return;
        }
        
        // Regular users can only access their own data
        if (!currentUser.getId().equals(targetUserId)) {
            throw new SecurityException("Access denied: Cannot access other user's data");
        }
    }
}