package com.expensetracker.app.controllers;

import com.expensetracker.app.models.Expense;
import com.expensetracker.app.repositories.ExpenseRepository;
import com.expensetracker.app.services.SecurityService;
import com.google.cloud.Timestamp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.expensetracker.app.dto.ExpenseRequest;
import com.expensetracker.app.dto.ApiResponse;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final SecurityService securityService;

    public ExpenseController(ExpenseRepository expenseRepository, SecurityService securityService) {
        this.expenseRepository = expenseRepository;
        this.securityService = securityService;
    }

    // GET /expenses/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Expense>> getExpensesByUserId(@PathVariable String userId) {
        securityService.validateUserAccess(userId);
        List<Expense> expenses = expenseRepository.findByUserId(userId);
        return ResponseEntity.ok(expenses);
    }

    // POST /expenses
    @PostMapping
    public ResponseEntity<Expense> createExpense(@RequestBody ExpenseRequest body) {
        if (body.getUserId() != null) {
            securityService.validateUserAccess(body.getUserId());
        }
        if (body.getDate() == null || body.getDate().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "date is required (MM/dd/yyyy)");
        }
        Timestamp ts;
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate ld = LocalDate.parse(body.getDate(), fmt);
            ts = Timestamp.of(java.util.Date.from(ld.atStartOfDay(ZoneOffset.UTC).toInstant()));
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format. Use MM/dd/yyyy (e.g. 10/02/2025)");
        }

        Expense expense = new Expense();
        expense.setDescription(body.getDescription());
        expense.setAmount(body.getAmount());
        expense.setCategory(body.getCategory());
        expense.setDate(ts);
        expense.setUserId(body.getUserId());
    expense.setGoalId(body.getGoalId());

        Expense saved = expenseRepository.createExpense(expense);
        return ResponseEntity.ok(saved);
    }

    public ResponseEntity<Expense> createExpense(Expense expense) {
        try {
            if (expense.getUserId() != null) {
                securityService.validateUserAccess(expense.getUserId());
            }
            if (expense.getDate() == null) {
                // keep legacy behavior for tests: default to now
                expense.setDate(Timestamp.now());
            }
            Expense saved = expenseRepository.createExpense(expense);
            return ResponseEntity.ok(saved);
        } catch (SecurityException se) {
            return ResponseEntity.status(403).build();
        } catch (IllegalArgumentException ie) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET /expenses/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable String id) {
        Optional<Expense> expenseOpt = expenseRepository.findById(id);
        if (expenseOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found");
        }
        Expense expense = expenseOpt.get();
        securityService.validateUserAccess(expense.getUserId());
        return ResponseEntity.ok(expense);
    }

    // PUT /expenses/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable String id, @RequestBody ExpenseRequest body) {
        Optional<Expense> existingOpt = expenseRepository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found");
        }
        Expense existing = existingOpt.get();
        securityService.validateUserAccess(existing.getUserId());
        if (body.getUserId() != null && !body.getUserId().equals(existing.getUserId())) {
            throw new SecurityException("Cannot change expense ownership");
        }
        Expense toUpdate = new Expense();
        toUpdate.setDescription(body.getDescription());
        toUpdate.setAmount(body.getAmount());
        toUpdate.setCategory(body.getCategory());
        if (body.getDate() != null && !body.getDate().isBlank()) {
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate ld = LocalDate.parse(body.getDate(), fmt);
                toUpdate.setDate(Timestamp.of(java.util.Date.from(ld.atStartOfDay(ZoneOffset.UTC).toInstant())));
            } catch (DateTimeParseException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format. Use MM/dd/yyyy (e.g. 10/02/2025)");
            }
        }
        toUpdate.setGoalId(body.getGoalId());
        toUpdate.setUserId(body.getUserId());

        Optional<Expense> updated = expenseRepository.update(id, toUpdate);
        return updated.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
    }

    public ResponseEntity<Expense> updateExpense(String id, Expense expense) {
        try {
            Optional<Expense> existingOpt = expenseRepository.findById(id);
            if (existingOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Expense existing = existingOpt.get();
            securityService.validateUserAccess(existing.getUserId());
            if (expense.getUserId() != null && !expense.getUserId().equals(existing.getUserId())) {
                throw new SecurityException("Cannot change expense ownership");
            }
            Optional<Expense> updated = expenseRepository.update(id, expense);
            return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (SecurityException se) {
            return ResponseEntity.status(403).build();
        } catch (IllegalArgumentException ie) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // DELETE /expenses/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@PathVariable String id) {
        Optional<Expense> existingOpt = expenseRepository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found");
        }
        Expense existing = existingOpt.get();
        securityService.validateUserAccess(existing.getUserId());
        expenseRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>("Expense deleted successfully", null));
    }
}