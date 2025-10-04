package com.expensetracker.app;

import com.expensetracker.app.models.User;
import com.expensetracker.app.models.Expense;
import com.expensetracker.app.repositories.UserRepository;
import com.expensetracker.app.repositories.ExpenseRepository;
import com.expensetracker.app.controllers.ExpenseController;
import com.expensetracker.app.dto.ExpenseRequest;
import com.expensetracker.app.services.SecurityService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SecurityTest {

    private UserRepository userRepository;
    private ExpenseRepository expenseRepository;
    private SecurityService securityService;
    private ExpenseController expenseController;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        expenseRepository = new ExpenseRepository();
        securityService = new SecurityService(userRepository);
        expenseController = new ExpenseController(expenseRepository, securityService);
    }

    @Test
    @DisplayName("Test: Security - Cross-user expense creation should be blocked")
    void testCrossUserExpenseCreationBlocked() throws Exception {
        User user1 = createUser("User1", "user1sec@test.com");
        User user2 = createUser("User2", "user2sec@test.com");

        securityService.setCurrentUser(user1.getId());

        ExpenseRequest body = new ExpenseRequest();
        body.setUserId(user2.getId()); // Different user!
        body.setDescription("Unauthorized expense");
        body.setAmount(new BigDecimal("100.00"));
        body.setCategory("OTHER");
        body.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

        // Should throw SecurityException when trying to create expense for different user
        assertThrows(SecurityException.class, () -> {
            expenseController.createExpense(body);
        }, "Expected SecurityException when creating expense for different user");
    }

    @Test
    @DisplayName("Test: Security - Same user expense creation should work")
    void testSameUserExpenseCreationAllowed() throws Exception {
        User user = createUser("Test User", "testsame@test.com");

        securityService.setCurrentUser(user.getId());

    ExpenseRequest body = new ExpenseRequest();
    body.setUserId(user.getId()); // Same user
    body.setDescription("Valid expense");
    body.setAmount(new BigDecimal("50.00"));
    body.setCategory("Food");
    body.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

    ResponseEntity<Expense> response = expenseController.createExpense(body);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    private User createUser(String name, String email) throws Exception {
        User user = new User();
        user.setName(name);
        // Add timestamp to make email unique across test runs
        String uniqueEmail = email.replace("@", "+" + System.currentTimeMillis() + "@");
        user.setEmail(uniqueEmail);
        user.setPassword("password123");
        return userRepository.createUser(user);
    }
}