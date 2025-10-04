package com.expensetracker.app.testutil;

import com.expensetracker.app.models.Goal;
import com.expensetracker.app.models.User;
import com.expensetracker.app.models.Expense;
import com.expensetracker.app.models.enums.UserRole;
import com.expensetracker.app.models.enums.GoalMode;
import com.expensetracker.app.models.enums.GoalStatus;
import com.google.cloud.Timestamp;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Test data builders for creating test entities with sensible defaults.
 */
public class TestDataBuilders {

    public static UserBuilder user() {
        return new UserBuilder();
    }

    public static GoalBuilder goal() {
        return new GoalBuilder();
    }

    public static ExpenseBuilder expense() {
        return new ExpenseBuilder();
    }

    public static class UserBuilder {
        private User user = new User();

        public UserBuilder() {
            user.setId("test-user-id");
            user.setName("Test User");
            user.setEmail("test@example.com");
            user.setRole(UserRole.USER);
            user.setCreatedAt(Timestamp.of(java.util.Date.from(Instant.now())));
        }

        public UserBuilder withId(String id) {
            user.setId(id);
            return this;
        }

        public UserBuilder withName(String name) {
            user.setName(name);
            return this;
        }

        public UserBuilder withEmail(String email) {
            user.setEmail(email);
            return this;
        }

        public UserBuilder withRole(UserRole role) {
            user.setRole(role);
            return this;
        }

        public UserBuilder asAdmin() {
            user.setRole(UserRole.ADMIN);
            return this;
        }

        public User build() {
            return user;
        }
    }

    public static class GoalBuilder {
        private Goal goal = new Goal();

        public GoalBuilder() {
            goal.setId("test-goal-id");
            goal.setUserId("test-user-id");
            goal.setTitle("Test Goal");
            goal.setDescription("Test Description");
            goal.setTargetAmount(new BigDecimal("1000.00"));
            goal.setCurrentAmount(new BigDecimal("0.00"));
            goal.setCategory("TEST");
            goal.setMode(GoalMode.LIMIT);
            goal.setStatus(GoalStatus.ACTIVE);
            goal.setCompleted(false);
            goal.setCreatedAt(Timestamp.of(java.util.Date.from(Instant.now())));
        }

        public GoalBuilder withId(String id) {
            goal.setId(id);
            return this;
        }

        public GoalBuilder withUserId(String userId) {
            goal.setUserId(userId);
            return this;
        }

        public GoalBuilder withTitle(String title) {
            goal.setTitle(title);
            return this;
        }

        public GoalBuilder withTargetAmount(BigDecimal amount) {
            goal.setTargetAmount(amount);
            return this;
        }

        public GoalBuilder withCurrentAmount(BigDecimal amount) {
            goal.setCurrentAmount(amount);
            return this;
        }

        public GoalBuilder withMode(GoalMode mode) {
            goal.setMode(mode);
            return this;
        }

        public GoalBuilder withStatus(GoalStatus status) {
            goal.setStatus(status);
            return this;
        }

        public GoalBuilder asInvestment() {
            goal.setMode(GoalMode.INVESTMENT);
            return this;
        }

        public GoalBuilder asLimit() {
            goal.setMode(GoalMode.LIMIT);
            return this;
        }

        public GoalBuilder withDates(Timestamp start, Timestamp end) {
            goal.setStartDate(start);
            goal.setEndDate(end);
            return this;
        }

        public Goal build() {
            return goal;
        }
    }

    public static class ExpenseBuilder {
        private Expense expense = new Expense();

        public ExpenseBuilder() {
            expense.setId("test-expense-id");
            expense.setUserId("test-user-id");
            expense.setDescription("Test Expense");
            expense.setAmount(new BigDecimal("50.00"));
            expense.setCategory("TEST");
            expense.setDate(Timestamp.of(java.util.Date.from(Instant.now())));
            expense.setCreatedAt(Timestamp.of(java.util.Date.from(Instant.now())));
        }

        public ExpenseBuilder withId(String id) {
            expense.setId(id);
            return this;
        }

        public ExpenseBuilder withUserId(String userId) {
            expense.setUserId(userId);
            return this;
        }

        public ExpenseBuilder withDescription(String description) {
            expense.setDescription(description);
            return this;
        }

        public ExpenseBuilder withAmount(BigDecimal amount) {
            expense.setAmount(amount);
            return this;
        }

        public ExpenseBuilder withCategory(String category) {
            expense.setCategory(category);
            return this;
        }

        public ExpenseBuilder withGoalId(String goalId) {
            expense.setGoalId(goalId);
            return this;
        }

        public ExpenseBuilder withDate(Timestamp date) {
            expense.setDate(date);
            return this;
        }

        public Expense build() {
            return expense;
        }
    }
}