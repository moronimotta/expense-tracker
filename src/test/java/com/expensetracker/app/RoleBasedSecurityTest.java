package com.expensetracker.app;

import com.expensetracker.app.models.User;
import com.expensetracker.app.models.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

public class RoleBasedSecurityTest {

    @Test
    @DisplayName("User default role is USER")
    void defaultRoleIsUser() {
        User u = new User();
        assertEquals(UserRole.USER, u.getRole());
    }

    @Test
    @DisplayName("Can set role to ADMIN")
    void canSetAdminRole() {
        User u = new User();
        u.setRole(UserRole.ADMIN);
        assertEquals(UserRole.ADMIN, u.getRole());
    }

    @Test
    @DisplayName("Can switch roles between USER and ADMIN")
    void canSwitchRoles() {
        User u = new User();
        u.setRole(UserRole.ADMIN);
        assertEquals(UserRole.ADMIN, u.getRole());
        u.setRole(UserRole.USER);
        assertEquals(UserRole.USER, u.getRole());
    }
}