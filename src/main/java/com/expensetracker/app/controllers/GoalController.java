package com.expensetracker.app.controllers;

import com.expensetracker.app.dto.ApiResponse;
import com.expensetracker.app.models.Goal;
import com.expensetracker.app.models.enums.GoalMode;
import com.expensetracker.app.repositories.GoalRepository;
import com.expensetracker.app.dto.GoalRequest;
import com.google.cloud.Timestamp;
import com.expensetracker.app.services.SecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/goals")
public class GoalController {

    private final GoalRepository goalRepository;
    private final SecurityService securityService;

    public GoalController(GoalRepository goalRepository, SecurityService securityService) {
        this.goalRepository = goalRepository;
        this.securityService = securityService;
    }

    // GET /goals
    @GetMapping
    public ResponseEntity<List<Goal>> getAllGoals() {
        securityService.requireAdmin();
        try {
            List<Goal> goals = goalRepository.findAll();
            return ResponseEntity.ok(goals);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get goals", e);
        }
    }

    // GET /goals/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoalById(@PathVariable String id) {
        try {
            Optional<Goal> goal = goalRepository.findById(id);
            if (goal.isPresent()) {
                return ResponseEntity.ok(goal.get());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found");
            }
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get goal", e);
        }
    }

    // GET /goals/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Goal>> getGoalsByUserId(@PathVariable String userId) {
        try {
            List<Goal> goals = goalRepository.findByUserId(userId);
            return ResponseEntity.ok(goals);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get goals for user", e);
        }
    }

    // GET /goals/user/{userId}/active
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<Goal>> getActiveGoalsByUserId(@PathVariable String userId) {
        try {
            List<Goal> goals = goalRepository.findActiveGoalsByUserId(userId);
            return ResponseEntity.ok(goals);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get active goals", e);
        }
    }

    // GET /goals/user/{userId}/analytics
    @GetMapping("/user/{userId}/analytics")
    public ResponseEntity<Map<String, Object>> getGoalAnalytics(@PathVariable String userId) {
        try {
            Map<String, Object> analytics = goalRepository.getGoalAnalytics(userId);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get goal analytics", e);
        }
    }

    // POST /goals (DTO with dates in MM/dd/yyyy)
    @PostMapping
    public ResponseEntity<Goal> createGoal(@RequestBody GoalRequest request) {
        try {
            Goal goal = new Goal();
            goal.setUserId(request.getUserId());
            goal.setTitle(request.getTitle());
            goal.setDescription(request.getDescription());
            goal.setTargetAmount(request.getTargetAmount());
            goal.setCategory(request.getCategory());

            if (request.getMode() != null && !request.getMode().isBlank()) {
                try {
                    goal.setMode(GoalMode.valueOf(request.getMode().trim().toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid mode. Use LIMIT or INVESTMENT");
                }
            }

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            if (request.getStartDate() != null && !request.getStartDate().isBlank()) {
                LocalDate sd = LocalDate.parse(request.getStartDate(), fmt);
                goal.setStartDate(Timestamp.of(java.util.Date.from(sd.atStartOfDay(ZoneOffset.UTC).toInstant())));
            }
            if (request.getEndDate() != null && !request.getEndDate().isBlank()) {
                LocalDate ed = LocalDate.parse(request.getEndDate(), fmt);
                goal.setEndDate(Timestamp.of(java.util.Date.from(ed.atStartOfDay(ZoneOffset.UTC).toInstant())));
            }

            Goal savedGoal = goalRepository.save(goal);
            return ResponseEntity.ok(savedGoal);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format. Use MM/dd/yyyy", e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create goal", e);
        }
    }

    // Overload for tests: accept Goal entity directly (no annotations/mapping)
    public ResponseEntity<Goal> createGoal(Goal goal) {
        try {
            if (goal == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Goal is required");
            }
            if (goal.getUserId() == null || goal.getUserId().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
            }
            if (goal.getStartDate() == null || goal.getEndDate() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "startDate and endDate are required");
            }
            Goal saved = goalRepository.save(goal);
            return ResponseEntity.ok(saved);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create goal", e);
        }
    }

    // PUT /goals/{id} (DTO with dates in MM/dd/yyyy, partial update)
    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(@PathVariable String id, @RequestBody GoalRequest request) {
        try {
            Goal patch = new Goal();
            patch.setUserId(request.getUserId());
            patch.setTitle(request.getTitle());
            patch.setDescription(request.getDescription());
            patch.setTargetAmount(request.getTargetAmount());
            patch.setCategory(request.getCategory());

            if (request.getMode() != null && !request.getMode().isBlank()) {
                try {
                    patch.setMode(GoalMode.valueOf(request.getMode().trim().toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid mode. Use LIMIT or INVESTMENT");
                }
            }

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            if (request.getStartDate() != null && !request.getStartDate().isBlank()) {
                LocalDate sd = LocalDate.parse(request.getStartDate(), fmt);
                patch.setStartDate(Timestamp.of(java.util.Date.from(sd.atStartOfDay(ZoneOffset.UTC).toInstant())));
            }
            if (request.getEndDate() != null && !request.getEndDate().isBlank()) {
                LocalDate ed = LocalDate.parse(request.getEndDate(), fmt);
                patch.setEndDate(Timestamp.of(java.util.Date.from(ed.atStartOfDay(ZoneOffset.UTC).toInstant())));
            }

            Optional<Goal> updatedGoal = goalRepository.update(id, patch);
            if (updatedGoal.isPresent()) {
                return ResponseEntity.ok(updatedGoal.get());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found");
            }
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format. Use MM/dd/yyyy", e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update goal", e);
        }
    }

    // PUT /goals/{id}/progress
    @PutMapping("/{id}/progress")
    public ResponseEntity<Goal> updateGoalProgress(@PathVariable String id) {
        try {
            Goal updatedGoal = goalRepository.updateGoalProgress(id);
            return ResponseEntity.ok(updatedGoal);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update goal progress", e);
        }
    }

    // PUT /goals/{id}/sync - recompute currentAmount from expenses linked by goalId
    @PutMapping("/{id}/sync")
    public ResponseEntity<ApiResponse<Goal>> syncGoal(@PathVariable String id) {
        try {
            Goal updated = goalRepository.sync(id);
            return ResponseEntity.ok(new ApiResponse<>("Goal synced successfully", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>("Failed to sync goal", null));
        }
    }

    // PUT /goals/user/{userId}/progress
    @PutMapping("/user/{userId}/progress")
    public ResponseEntity<List<Goal>> updateAllGoalProgressForUser(@PathVariable String userId) {
        try {
            List<Goal> updatedGoals = goalRepository.updateAllGoalProgressForUser(userId);
            return ResponseEntity.ok(updatedGoals);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update goal progress for user", e);
        }
    }

    // DELETE /goals/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGoal(@PathVariable String id) {
        try {
            boolean deleted = goalRepository.deleteById(id);
            if (deleted) {
                return ResponseEntity.ok(new ApiResponse<>("Goal deleted successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Goal not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>("Failed to delete goal", null));
        }
    }

    // DELETE /goals/user/{userId}
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteAllGoalsForUser(@PathVariable String userId) {
        try {
            goalRepository.deleteByUserId(userId);
            return ResponseEntity.ok(new ApiResponse<>("All goals for user deleted", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>("Failed to delete goals for user", null));
        }
    }
}