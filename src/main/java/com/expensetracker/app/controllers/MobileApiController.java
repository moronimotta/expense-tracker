package com.expensetracker.app.controllers;

import com.expensetracker.app.dto.ExpenseRequest;
import com.expensetracker.app.dto.GoalRequest;
import com.expensetracker.app.models.Expense;
import com.expensetracker.app.models.Goal;
import com.expensetracker.app.models.User;
import com.expensetracker.app.repositories.ExpenseRepository;
import com.expensetracker.app.repositories.GoalRepository;
import com.expensetracker.app.services.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MobileApiController {

    private final SecurityService securityService;
    private final ExpenseRepository expenseRepository;
    private final GoalRepository goalRepository;

    public MobileApiController(SecurityService securityService, ExpenseRepository expenseRepository, GoalRepository goalRepository) {
        this.securityService = securityService;
        this.expenseRepository = expenseRepository;
        this.goalRepository = goalRepository;
    }

    @GetMapping("/user")
    public ResponseEntity<User> getCurrentUser() {
        User u = securityService.getCurrentUser();
        return ResponseEntity.ok(u);
    }

    @GetMapping("/expenses")
    public ResponseEntity<List<Expense>> myExpenses() {
        String userId = securityService.getCurrentUserId();
        List<Expense> expenses = expenseRepository.findByUserId(userId);
        return ResponseEntity.ok(expenses);
    }

    @PostMapping("/expenses")
    public ResponseEntity<Expense> createExpense(@RequestBody ExpenseRequest body) {
        String userId = securityService.getCurrentUserId();
        body.setUserId(userId);
        ExpenseController helper = new ExpenseController(expenseRepository, securityService);
        return helper.createExpense(body);
    }

    @GetMapping("/goals")
    public ResponseEntity<List<Goal>> myGoals() {
        String userId = securityService.getCurrentUserId();
        List<Goal> goals = goalRepository.findByUserId(userId);
        return ResponseEntity.ok(goals);
    }

    @PostMapping("/goals")
    public ResponseEntity<Goal> createGoal(@RequestBody GoalRequest body) {
        String userId = securityService.getCurrentUserId();
        body.setUserId(userId);
        GoalController helper = new GoalController(goalRepository, securityService);
        return helper.createGoal(body);
    }
}
