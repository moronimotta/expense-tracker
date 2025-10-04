package com.expensetracker.app.repositories;

import com.expensetracker.app.models.Expense;
import com.expensetracker.app.models.Goal;
import com.expensetracker.app.models.enums.GoalMode;
import com.expensetracker.app.models.enums.GoalStatus;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class GoalRepository {

    private static final String COLLECTION_NAME = "goals";
    private final ExpenseRepository expenseRepository;

    public GoalRepository(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Goal save(Goal goal) {
        validate(goal);
        if (goal.getId() == null || goal.getId().isEmpty()) {
            goal.setId(UUID.randomUUID().toString());
        }
        if (goal.getCurrentAmount() == null) {
            goal.setCurrentAmount(BigDecimal.ZERO);
        }
        try {
            Firestore db = FirestoreClient.getFirestore();
            db.collection(COLLECTION_NAME).document(goal.getId()).set(goal).get();
            return goal;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save goal: " + e.getMessage(), e);
        }
    }

    public Optional<Goal> findById(String id) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            Goal goal = db.collection(COLLECTION_NAME).document(id).get().get().toObject(Goal.class);
            if (goal != null) {
                goal.setId(id);
                if (goal.getDeletedAt() != null) return Optional.empty();
            }
            return Optional.ofNullable(goal);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find goal: " + e.getMessage(), e);
        }
    }

    public List<Goal> findByUserId(String userId) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            List<QueryDocumentSnapshot> docs = db.collection(COLLECTION_NAME)
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("deletedAt", null)
                    .get().get().getDocuments();
            List<Goal> list = new ArrayList<>();
            for (QueryDocumentSnapshot d : docs) {
                Goal g = d.toObject(Goal.class);
                if (g != null) {
                    g.setId(d.getId());
                    list.add(g);
                }
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find goals by userId: " + e.getMessage(), e);
        }
    }

    public List<Goal> findAll() {
        try {
            Firestore db = FirestoreClient.getFirestore();
            List<QueryDocumentSnapshot> docs = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deletedAt", null)
                    .get().get().getDocuments();
            List<Goal> list = new ArrayList<>();
            for (QueryDocumentSnapshot d : docs) {
                Goal g = d.toObject(Goal.class);
                if (g != null) {
                    g.setId(d.getId());
                    list.add(g);
                }
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("Failed to list goals: " + e.getMessage(), e);
        }
    }

    public List<Goal> findActiveGoalsByUserId(String userId) {
        Date now = Timestamp.now().toDate();
        return findByUserId(userId).stream()
                .filter(g -> !g.isCompleted())
                .filter(g -> g.getStartDate().toDate().before(now) && g.getEndDate().toDate().after(now))
                .collect(Collectors.toList());
    }

    public Goal updateGoalProgress(String goalId) {
        Goal goal = findById(goalId).orElseThrow(() -> new IllegalArgumentException("Goal not found: " + goalId));

        BigDecimal totalExpenses = expenseRepository.calculateTotalByUserIdAndDateRange(
                goal.getUserId(),
                goal.getStartDate(),
                goal.getEndDate()
        );

        if (goal.getCategory() != null && !goal.getCategory().trim().isEmpty()) {
            totalExpenses = expenseRepository.findByUserIdAndDateRange(
                    goal.getUserId(),
                    goal.getStartDate(),
                    goal.getEndDate()
            ).stream()
             .filter(e -> e.getCategory().equalsIgnoreCase(goal.getCategory()))
             .map(Expense::getAmount)
             .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        goal.setCurrentAmount(totalExpenses);
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setCompleted(true);
        }
        try {
            Firestore db = FirestoreClient.getFirestore();
            db.collection(COLLECTION_NAME).document(goal.getId()).set(goal).get();
            return goal;
        } catch (Exception e) {
            throw new RuntimeException("Failed to persist goal progress: " + e.getMessage(), e);
        }
    }

    public List<Goal> updateAllGoalProgressForUser(String userId) {
        return findByUserId(userId).stream()
                .map(g -> updateGoalProgress(g.getId()))
                .collect(Collectors.toList());
    }

    public Goal sync(String goalId) {
        Goal goal = findById(goalId).orElseThrow(() -> new IllegalArgumentException("Goal not found: " + goalId));

        BigDecimal total = (goal.getStartDate() != null && goal.getEndDate() != null)
                ? expenseRepository.calculateTotalByGoal(goalId, goal.getStartDate(), goal.getEndDate())
                : expenseRepository.calculateTotalByGoal(goalId, null, null);

        if (total == null) total = BigDecimal.ZERO;
        goal.setCurrentAmount(total);

        if (goal.getTargetAmount() != null) {
            int cmp = total.compareTo(goal.getTargetAmount());
            if (goal.getMode() == GoalMode.LIMIT) {
                goal.setStatus(cmp > 0 ? GoalStatus.EXCEEDED : GoalStatus.UNDER_LIMIT);
            } else if (goal.getMode() == GoalMode.INVESTMENT) {
                goal.setStatus(cmp >= 0 ? GoalStatus.SURPASSED : GoalStatus.ACTIVE);
            }
            if (cmp >= 0) {
                goal.setCompleted(true);
            }
        }

        try {
            Firestore db = FirestoreClient.getFirestore();
            db.collection(COLLECTION_NAME).document(goal.getId()).set(goal).get();
            return goal;
        } catch (Exception e) {
            throw new RuntimeException("Failed to sync goal: " + e.getMessage(), e);
        }
    }

    public Optional<Goal> update(String id, Goal updated) {
        try {
            Optional<Goal> existingOpt = findById(id);
            if (existingOpt.isEmpty()) return Optional.empty();
            Goal existing = existingOpt.get();

            Map<String, Object> updates = new HashMap<>();
            if (updated.getTitle() != null) updates.put("title", updated.getTitle());
            if (updated.getDescription() != null) updates.put("description", updated.getDescription());
            if (updated.getTargetAmount() != null) updates.put("targetAmount", updated.getTargetAmount());
            if (updated.getCurrentAmount() != null) updates.put("currentAmount", updated.getCurrentAmount());
            if (updated.getStartDate() != null) updates.put("startDate", updated.getStartDate());
            if (updated.getEndDate() != null) updates.put("endDate", updated.getEndDate());
            if (updated.getCategory() != null) updates.put("category", updated.getCategory());
            if (updated.getMode() != null) updates.put("mode", updated.getMode());
            if (updated.getStatus() != null) updates.put("status", updated.getStatus());
            updates.put("updatedAt", Timestamp.now());

            if (updates.size() <= 1) return existingOpt; // only updatedAt

            Firestore db = FirestoreClient.getFirestore();
            db.collection(COLLECTION_NAME).document(id).update(updates).get();
            return findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update goal: " + e.getMessage(), e);
        }
    }

    public boolean deleteById(String id) {
        try {
            Optional<Goal> existingOpt = findById(id);
            if (existingOpt.isEmpty()) return false;
            Firestore db = FirestoreClient.getFirestore();
            Map<String, Object> updates = new HashMap<>();
            updates.put("deletedAt", Timestamp.now());
            updates.put("updatedAt", Timestamp.now());
            db.collection(COLLECTION_NAME).document(id).update(updates).get();
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete goal: " + e.getMessage(), e);
        }
    }

    public void deleteByUserId(String userId) {
        for (Goal g : findByUserId(userId)) {
            deleteById(g.getId());
        }
    }

    public Map<String, Object> getGoalAnalytics(String userId) {
        List<Goal> userGoals = findByUserId(userId);
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalGoals", userGoals.size());
        analytics.put("completedGoals", userGoals.stream().filter(Goal::isCompleted).count());
        analytics.put("activeGoals", (long) findActiveGoalsByUserId(userId).size());
        analytics.put("totalTargetAmount", userGoals.stream().map(Goal::getTargetAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        analytics.put("totalCurrentAmount", userGoals.stream().map(Goal::getCurrentAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        return analytics;
    }

    private void validate(Goal goal) {
        if (goal.getUserId() == null) throw new IllegalArgumentException("User ID is required");
        if (goal.getTitle() == null || goal.getTitle().trim().isEmpty()) throw new IllegalArgumentException("Title is required");
        if (goal.getTargetAmount() == null || goal.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Target amount must be positive");
        if (goal.getStartDate() == null) throw new IllegalArgumentException("Start date is required");
        if (goal.getEndDate() == null) throw new IllegalArgumentException("End date is required");
        if (goal.getEndDate().toDate().before(goal.getStartDate().toDate()))
            throw new IllegalArgumentException("End date must be after start date");
    }
}