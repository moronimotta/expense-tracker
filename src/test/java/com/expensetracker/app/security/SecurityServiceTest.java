package com.expensetracker.app.security;

import com.expensetracker.app.models.User;
import com.expensetracker.app.models.enums.UserRole;
import com.expensetracker.app.repositories.UserRepository;
import com.expensetracker.app.services.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SecurityServiceTest {

    private SecurityService securityService;
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        securityService = new SecurityService(userRepository);
        // inject mock (field is package-private via reflection if needed)
        try {
            java.lang.reflect.Field f = SecurityService.class.getDeclaredField("userRepository");
            f.setAccessible(true);
            f.set(securityService, userRepository);
        } catch (Exception e) {
            fail("Failed to inject mock UserRepository: " + e.getMessage());
        }
    }

    private User buildUser(String id, UserRole role) {
        User u = new User();
        u.setId(id);
        u.setRole(role);
        u.setEmail(id + "@mail");
        u.setName("Name " + id);
        return u;
    }

    @Test
    void requireAdmin_allowsAdmin() throws Exception {
        String adminId = "admin-1";
        securityService.setCurrentUser(adminId);
        when(userRepository.findById(adminId)).thenReturn(buildUser(adminId, UserRole.ADMIN));
        assertDoesNotThrow(() -> securityService.requireAdmin());
    }

    @Test
    void requireAdmin_deniesNonAdmin() throws Exception {
        String userId = "user-1";
        securityService.setCurrentUser(userId);
        when(userRepository.findById(userId)).thenReturn(buildUser(userId, UserRole.USER));
        SecurityException ex = assertThrows(SecurityException.class, () -> securityService.requireAdmin());
        assertTrue(ex.getMessage().toLowerCase().contains("admin"));
    }

    @Test
    void validateUserAccess_allowsSelf() throws Exception {
        String userId = "u1";
        securityService.setCurrentUser(userId);
        when(userRepository.findById(userId)).thenReturn(buildUser(userId, UserRole.USER));
        assertDoesNotThrow(() -> securityService.validateUserAccess(userId));
    }

    @Test
    void validateUserAccess_deniesOtherUserWhenNotAdmin() throws Exception {
        String userId = "u1";
        securityService.setCurrentUser(userId);
        when(userRepository.findById(userId)).thenReturn(buildUser(userId, UserRole.USER));
        SecurityException ex = assertThrows(SecurityException.class, () -> securityService.validateUserAccess("other"));
        assertTrue(ex.getMessage().toLowerCase().contains("access denied"));
    }

    @Test
    void validateUserAccess_adminCanAccessOthers() throws Exception {
        String adminId = "admin-2";
        securityService.setCurrentUser(adminId);
        when(userRepository.findById(adminId)).thenReturn(buildUser(adminId, UserRole.ADMIN));
        assertDoesNotThrow(() -> securityService.validateUserAccess("someone-else"));
    }
}
