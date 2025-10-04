package com.expensetracker.app.repositories;

import com.expensetracker.app.models.Expense;
import com.expensetracker.app.models.enums.ExpenseCategory;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.api.gax.rpc.FailedPreconditionException;
import org.springframework.stereotype.Repository;
import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class ExpenseRepository {

    private static final String COLLECTION_NAME = "expenses";

    public Expense createExpense(Expense expense) {
        validate(expense);
        if (expense.getId() == null || expense.getId().isEmpty()) {
            expense.setId(UUID.randomUUID().toString());
        }
        try {
            Firestore db = FirestoreClient.getFirestore();
            db.collection(COLLECTION_NAME)
              .document(expense.getId())
              .set(expense)
              .get();
            return expense;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create expense: " + e.getMessage(), e);
        }
    }

    public Optional<Expense> findById(String id) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            Expense expense = db.collection(COLLECTION_NAME)
                    .document(id)
                    .get()
                    .get()
                    .toObject(Expense.class);
            if (expense != null) {
                expense.setId(id);
                if (expense.getDeletedAt() != null) return Optional.empty();
            }
            return Optional.ofNullable(expense);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find expense by id: " + e.getMessage(), e);
        }
    }

    public List<Expense> findByUserId(String userId) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            List<QueryDocumentSnapshot> docs = db.collection(COLLECTION_NAME)
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("deletedAt", null)
                    .get()
                    .get()
                    .getDocuments();
            List<Expense> list = new ArrayList<>();
            for (QueryDocumentSnapshot d : docs) {
                Expense exp = d.toObject(Expense.class);
                if (exp != null) {
                    exp.setId(d.getId());
                    list.add(exp);
                }
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find expenses by userId: " + e.getMessage(), e);
        }
    }

    public List<Expense> findAll() {
        try {
            Firestore db = FirestoreClient.getFirestore();
            List<QueryDocumentSnapshot> docs = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deletedAt", null)
                    .get()
                    .get()
                    .getDocuments();
            List<Expense> list = new ArrayList<>();
            for (QueryDocumentSnapshot d : docs) {
                Expense exp = d.toObject(Expense.class);
                if (exp != null) {
                    exp.setId(d.getId());
                    list.add(exp);
                }
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("Failed to list expenses: " + e.getMessage(), e);
        }
    }

    public List<Expense> findByUserIdAndDateRange(String userId, Timestamp startDate, Timestamp endDate) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            Query q = db.collection(COLLECTION_NAME)
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("deletedAt", null)
                    .whereGreaterThanOrEqualTo("date", startDate)
                    .whereLessThanOrEqualTo("date", endDate);
            List<QueryDocumentSnapshot> docs = q.get().get().getDocuments();
            List<Expense> list = new ArrayList<>();
            for (QueryDocumentSnapshot d : docs) {
                Expense exp = d.toObject(Expense.class);
                if (exp != null) {
                    exp.setId(d.getId());
                    list.add(exp);
                }
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("Failed to query expenses by date range: " + e.getMessage(), e);
        }
    }

    public List<Expense> findByUserIdAndDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        return findByUserIdAndDateRange(
                userId,
                Timestamp.of(java.sql.Timestamp.valueOf(startDate)),
                Timestamp.of(java.sql.Timestamp.valueOf(endDate))
        );
    }

    public Optional<Expense> update(String id, Expense updatedExpense) {
        try {
            Optional<Expense> existingOpt = findById(id);
            if (existingOpt.isEmpty()) return Optional.empty();
            Expense existing = existingOpt.get();

            Map<String, Object> updates = new HashMap<>();
            if (updatedExpense.getDescription() != null) updates.put("description", updatedExpense.getDescription());
            if (updatedExpense.getAmount() != null) updates.put("amount", updatedExpense.getAmount());
            if (updatedExpense.getCategory() != null) {
                if (!ExpenseCategory.isValid(updatedExpense.getCategory())) {
                    throw new IllegalArgumentException("Invalid category '" + updatedExpense.getCategory() + "'. Allowed: " + ExpenseCategory.allowedList());
                }
                updates.put("category", updatedExpense.getCategory());
            }
            if (updatedExpense.getDate() != null) updates.put("date", updatedExpense.getDate());
            if (updatedExpense.getUserId() != null && updatedExpense.getUserId().equals(existing.getUserId())) {
                updates.put("userId", updatedExpense.getUserId());
            }
            updates.put("updatedAt", Timestamp.now());

            if (updates.size() <= 1) { 
                return existingOpt;
            }

            Firestore db = FirestoreClient.getFirestore();
            db.collection(COLLECTION_NAME)
              .document(id)
              .update(updates)
              .get();

            return findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update expense: " + e.getMessage(), e);
        }
    }

    public boolean deleteById(String id) {
        try {
            Optional<Expense> existingOpt = findById(id);
            if (existingOpt.isEmpty()) return false;
            Firestore db = FirestoreClient.getFirestore();
            Map<String, Object> updates = new HashMap<>();
            updates.put("deletedAt", Timestamp.now());
            updates.put("updatedAt", Timestamp.now());
            db.collection(COLLECTION_NAME).document(id).update(updates).get();
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete expense: " + e.getMessage(), e);
        }
    }

    public void deleteByUserId(String userId) {
        try {
            for (Expense e : findByUserId(userId)) {
                deleteById(e.getId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete expenses by userId: " + e.getMessage(), e);
        }
    }

      public List<Expense> findByGoalId(String goalId) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            List<QueryDocumentSnapshot> docs = db.collection(COLLECTION_NAME)
                    .whereEqualTo("goalId", goalId)
                    .get()
                    .get()
                    .getDocuments();
            List<Expense> list = new ArrayList<>();
            for (QueryDocumentSnapshot d : docs) {
                Expense exp = d.toObject(Expense.class);
                if (exp != null) {
                    exp.setId(d.getId());

                    if (exp.getAmount() == null) {
                        Object amt = d.get("amount");
                        if (amt instanceof Number n) exp.setAmount(new BigDecimal(n.toString()));
                        else if (amt instanceof String s) {
                            try { exp.setAmount(new BigDecimal(s)); } catch (Exception ignore) {}
                        }
                    }
                    if (exp.getDate() == null) {
                        Timestamp ts = d.get("date", Timestamp.class);
                        if (ts != null) exp.setDate(ts);
                    }
                    if (exp.getGoalId() == null) {
                        String gid = d.getString("goalId");
                        if (gid != null) exp.setGoalId(gid);
                    }

                    if (exp.getDeletedAt() == null) {
                        list.add(exp);
                    }
                }
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find expenses by goalId: " + e.getMessage(), e);
        }
    }

    public List<Expense> findByGoalIdAndDateRange(String goalId, Timestamp startDate, Timestamp endDate) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            Query q = db.collection(COLLECTION_NAME)
                    .whereEqualTo("goalId", goalId)
                    .whereGreaterThanOrEqualTo("date", startDate)
                    .whereLessThanOrEqualTo("date", endDate);
            List<QueryDocumentSnapshot> docs = q.get().get().getDocuments();
            List<Expense> list = new ArrayList<>();
            for (QueryDocumentSnapshot d : docs) {
                Expense exp = d.toObject(Expense.class);
                if (exp != null) {
                    exp.setId(d.getId());

                    if (exp.getAmount() == null) {
                        Object amt = d.get("amount");
                        if (amt instanceof Number n) exp.setAmount(new BigDecimal(n.toString()));
                        else if (amt instanceof String s) {
                            try { exp.setAmount(new BigDecimal(s)); } catch (Exception ignore) {}
                        }
                    }
                    if (exp.getDate() == null) {
                        Timestamp ts = d.get("date", Timestamp.class);
                        if (ts != null) exp.setDate(ts);
                    }
                    if (exp.getGoalId() == null) {
                        String gid = d.getString("goalId");
                        if (gid != null) exp.setGoalId(gid);
                    }

                    if (exp.getDeletedAt() == null) {
                        list.add(exp);
                    }
                }
            }
            return list;
        } catch (Exception e) {
            if (e.getCause() instanceof FailedPreconditionException || e instanceof FailedPreconditionException) {
                List<Expense> allByGoal = findByGoalId(goalId);
                return allByGoal.stream()
                        .filter(ex -> ex.getDate() != null
                                && ex.getDate().compareTo(startDate) >= 0
                                && ex.getDate().compareTo(endDate) <= 0)
                        .collect(Collectors.toList());
            }
            throw new RuntimeException("Failed to find expenses by goalId in date range: " + e.getMessage(), e);
        }
    }

    public BigDecimal calculateTotalByGoal(String goalId, Timestamp startDate, Timestamp endDate) {
        List<Expense> items;
        if (startDate != null && endDate != null) {
            items = findByGoalIdAndDateRange(goalId, startDate, endDate);
        } else {
            items = findByGoalId(goalId);
        }
        return items.stream()
                .map(Expense::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal calculateTotalByUserIdAndDateRange(String userId, Timestamp startDate, Timestamp endDate) {
        return findByUserIdAndDateRange(userId, startDate, endDate).stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalByUserIdAndDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        return calculateTotalByUserIdAndDateRange(
                userId,
                Timestamp.of(java.sql.Timestamp.valueOf(startDate)),
                Timestamp.of(java.sql.Timestamp.valueOf(endDate))
        );
    }

    private void validate(Expense expense) {
        if (expense.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (expense.getAmount() == null || expense.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (expense.getDescription() == null || expense.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (expense.getCategory() == null || expense.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Category is required");
        }
        if (!ExpenseCategory.isValid(expense.getCategory())) {
            throw new IllegalArgumentException("Invalid category '" + expense.getCategory() + "'. Allowed: " + ExpenseCategory.allowedList());
        }
        if (expense.getDate() == null) {
            throw new IllegalArgumentException("Date is required");
        }
    }
}